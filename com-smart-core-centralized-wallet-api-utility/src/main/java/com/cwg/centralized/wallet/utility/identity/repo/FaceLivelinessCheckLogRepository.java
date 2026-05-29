package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.FaceLivelinessCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**

 @author SmartCore Contributors
 */
public interface FaceLivelinessCheckLogRepository extends JpaRepository<FaceLivelinessCheckLog, Long> {

    FaceLivelinessCheckLog findByWalletNo(String walletNo);

}
