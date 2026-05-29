package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.NinIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface NinIdentityLogRepository extends JpaRepository<NinIdentityLog, Long> {

    NinIdentityLog findByWalletNo(String walletNo);

    boolean existsNinIdentityLogByWalletNo(String walletNo);
}
