package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.VNinIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**

 @author SmartCore Contributors
 */
public interface VNinIdentityLogRepository extends JpaRepository<VNinIdentityLog, Long> {

    VNinIdentityLog findByWalletNo(String walletNo);

    boolean existsNinIdentityLogByWalletNo(String walletNo);
}
