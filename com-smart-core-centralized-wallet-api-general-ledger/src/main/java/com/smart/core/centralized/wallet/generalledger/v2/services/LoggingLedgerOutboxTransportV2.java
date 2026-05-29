package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingLedgerOutboxTransportV2 implements LedgerOutboxTransportV2 {

    @Override
    public void publish(LedgerOutboxEventV2 event) {
        log.info("Ledger outbox event ready for external publish. id={}, productCode={}, eventType={}, aggregateRef={}",
                event.getId(), event.getProductCode(), event.getEventType(), event.getAggregateRef());
    }
}
