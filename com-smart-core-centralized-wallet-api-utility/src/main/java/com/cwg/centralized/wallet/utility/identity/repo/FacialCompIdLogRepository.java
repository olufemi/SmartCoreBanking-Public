package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.FacialCompIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface FacialCompIdLogRepository extends JpaRepository<FacialCompIdentityLog, Long> {

    FacialCompIdentityLog findByWalletNo(String walletNo);

}
