package com.smart.core.centralized.wallet.generalledger.v2.repository;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface LedgerOutboxEventV2Repo extends JpaRepository<LedgerOutboxEventV2, String> {

    List<LedgerOutboxEventV2> findTop100ByStatusOrderByCreatedAtAsc(String status);

    List<LedgerOutboxEventV2> findByProductCodeAndStatusOrderByCreatedAtDesc(String productCode, String status, Pageable pageable);

    Optional<LedgerOutboxEventV2> findByIdAndProductCode(String id, String productCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from LedgerOutboxEventV2 e where e.status = :status order by e.createdAt asc")
    List<LedgerOutboxEventV2> findForUpdateByStatus(@Param("status") String status, Pageable pageable);

    boolean existsByProductCodeAndEventTypeAndAggregateRef(String productCode, String eventType, String aggregateRef);
}
