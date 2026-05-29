package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackofficeRoleRepo extends JpaRepository<BackofficeRole, Long> {

    boolean existsByRoleCode(String roleCode);

    Optional<BackofficeRole> findByRoleCode(String roleCode);
}
