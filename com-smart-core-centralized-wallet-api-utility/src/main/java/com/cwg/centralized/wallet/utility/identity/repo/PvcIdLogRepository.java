package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.PvcIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface PvcIdLogRepository extends JpaRepository<PvcIdentityLog, Long> {

    PvcIdentityLog findByWalletNo(String walletNo);

    boolean existsPvcIdentityLogByWalletNo(String walletNo);
}
