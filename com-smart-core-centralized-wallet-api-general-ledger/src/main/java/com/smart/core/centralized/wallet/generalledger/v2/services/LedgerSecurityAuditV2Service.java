package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerSecurityEventV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerSecurityEventV2Repo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LedgerSecurityAuditV2Service {

    private final LedgerSecurityEventV2Repo securityEventRepo;

    public LedgerSecurityAuditV2Service(LedgerSecurityEventV2Repo securityEventRepo) {
        this.securityEventRepo = securityEventRepo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String productCode,
                       String eventType,
                       String severity,
                       String aggregateRef,
                       String requestHash,
                       String reason,
                       String payload) {
        try {
            LedgerSecurityEventV2 event = new LedgerSecurityEventV2();
            event.setId(String.valueOf(GlobalMethods.generateTransactionId()));
            event.setProductCode(productCode);
            event.setEventType(eventType);
            event.setSeverity(severity);
            event.setAggregateRef(aggregateRef);
            event.setRequestHash(requestHash);
            event.setReason(reason);
            event.setPayload(payload);
            securityEventRepo.save(event);
        } catch (Exception ex) {
            log.error("Unable to persist ledger security event", ex);
        }
    }
}
