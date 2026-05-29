package com.smart.core.centralized.wallet.generalledger.v2.repository;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerSecurityEventV2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LedgerSecurityEventV2Repo extends JpaRepository<LedgerSecurityEventV2, String> {

    List<LedgerSecurityEventV2> findTop100ByProductCodeOrderByCreatedAtDesc(String productCode);

    @Query("select e from LedgerSecurityEventV2 e "
            + "where e.productCode = :productCode "
            + "and (:severity is null or e.severity = :severity) "
            + "and (:eventType is null or e.eventType = :eventType) "
            + "order by e.createdAt desc")
    List<LedgerSecurityEventV2> searchRecent(
            @Param("productCode") String productCode,
            @Param("severity") String severity,
            @Param("eventType") String eventType,
            Pageable pageable);
}
