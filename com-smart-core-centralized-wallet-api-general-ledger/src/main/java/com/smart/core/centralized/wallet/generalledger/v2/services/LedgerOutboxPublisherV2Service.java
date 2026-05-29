package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class LedgerOutboxPublisherV2Service {

    private final LedgerOutboxPublisherStateV2Service stateService;
    private final LedgerOutboxTransportV2 transport;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Value("${smartcore.ledger.outbox.publisher.enabled:false}")
    private boolean enabled;

    @Value("${smartcore.ledger.outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${smartcore.ledger.outbox.publisher.max-retries:5}")
    private int maxRetries;

    public LedgerOutboxPublisherV2Service(LedgerOutboxPublisherStateV2Service stateService,
                                          LedgerOutboxTransportV2 transport) {
        this.stateService = stateService;
        this.transport = transport;
    }

    @Scheduled(
            initialDelayString = "${smartcore.ledger.outbox.publisher.initial-delay-ms:30000}",
            fixedDelayString = "${smartcore.ledger.outbox.publisher.fixed-delay-ms:30000}"
    )
    public void publishPendingEvents() {
        if (!enabled || !running.compareAndSet(false, true)) {
            return;
        }

        try {
            List<LedgerOutboxEventV2> events = stateService.claimPending(batchSize);
            for (LedgerOutboxEventV2 event : events) {
                publishOne(event);
            }
        } finally {
            running.set(false);
        }
    }

    public void publishOne(LedgerOutboxEventV2 event) {
        try {
            transport.publish(event);
            stateService.markPublished(event.getId());
        } catch (Exception ex) {
            log.error("Ledger outbox publish failed. eventId={}, eventType={}, aggregateRef={}",
                    event.getId(), event.getEventType(), event.getAggregateRef(), ex);
            stateService.markFailed(event.getId(), maxRetries, ex.getMessage());
        }
    }
}
