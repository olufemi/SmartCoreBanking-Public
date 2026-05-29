package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerOutboxEventV2Repo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LedgerOutboxPublisherStateV2Service {

    private final LedgerOutboxEventV2Repo outboxRepo;

    public LedgerOutboxPublisherStateV2Service(LedgerOutboxEventV2Repo outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    @Transactional
    public List<LedgerOutboxEventV2> claimPending(int batchSize) {
        List<LedgerOutboxEventV2> events = outboxRepo.findForUpdateByStatus("PENDING", PageRequest.of(0, Math.max(1, batchSize)));
        for (LedgerOutboxEventV2 event : events) {
            event.setStatus("PROCESSING");
            event.setLastError(null);
            outboxRepo.save(event);
        }
        return events;
    }

    @Transactional
    public void markPublished(String eventId) {
        Optional<LedgerOutboxEventV2> eventOpt = outboxRepo.findById(eventId);
        if (!eventOpt.isPresent()) {
            return;
        }
        LedgerOutboxEventV2 event = eventOpt.get();
        event.setStatus("PUBLISHED");
        event.setPublishedAt(LocalDateTime.now());
        event.setLastError(null);
        outboxRepo.save(event);
    }

    @Transactional
    public void markFailed(String eventId, int maxRetries, String error) {
        Optional<LedgerOutboxEventV2> eventOpt = outboxRepo.findById(eventId);
        if (!eventOpt.isPresent()) {
            return;
        }
        LedgerOutboxEventV2 event = eventOpt.get();
        int retryCount = event.getRetryCount() == null ? 0 : event.getRetryCount();
        retryCount++;
        event.setRetryCount(retryCount);
        event.setLastError(truncate(error, 500));
        event.setStatus(retryCount >= maxRetries ? "FAILED" : "PENDING");
        outboxRepo.save(event);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
