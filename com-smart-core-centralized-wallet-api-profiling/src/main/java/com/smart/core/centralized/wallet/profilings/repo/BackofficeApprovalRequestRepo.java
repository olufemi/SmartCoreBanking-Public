package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeApprovalRequest;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackofficeApprovalRequestRepo extends JpaRepository<BackofficeApprovalRequest, Long> {

    Optional<BackofficeApprovalRequest> findByApprovalRef(String approvalRef);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select approval from BackofficeApprovalRequest approval where approval.approvalRef = :approvalRef")
    Optional<BackofficeApprovalRequest> findForUpdateByApprovalRef(@Param("approvalRef") String approvalRef);

    List<BackofficeApprovalRequest> findByStatusOrderByRequestedAtDesc(String status);

    List<BackofficeApprovalRequest> findByProductCodeAndStatusOrderByRequestedAtDesc(String productCode, String status);
}
