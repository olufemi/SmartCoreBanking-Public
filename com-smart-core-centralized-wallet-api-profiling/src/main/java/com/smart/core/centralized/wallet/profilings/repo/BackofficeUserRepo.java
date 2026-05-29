package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackofficeUserRepo extends JpaRepository<BackofficeUser, Long> {

    boolean existsByOperatorId(String operatorId);

    Optional<BackofficeUser> findByOperatorId(String operatorId);

    Optional<BackofficeUser> findFirstByEmailAddressIgnoreCase(String emailAddress);

    List<BackofficeUser> findByProductCodeOrderByCreatedAtDesc(String productCode);
}
