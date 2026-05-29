package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.BvnIdentityLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**

 @author SmartCore Contributors
 */
public interface BvnIdentityLogRepository extends JpaRepository<BvnIdentityLog, Long> {

    BvnIdentityLog findByWalletNo(String walletNo);

    BvnIdentityLog findByBvn(String bvn);

    boolean existsBvnIdentityLogByWalletNo(String walletNo);

    @Query("select bs from BvnIdentityLog bs where bs.logId=:logId")
    BvnIdentityLog findByBvnLogId(Long logId);

    @Query("select bs from BvnIdentityLog bs where bs.requestId=:requestId")
    List<BvnIdentityLog> findByRequestId(String requestId);

}
