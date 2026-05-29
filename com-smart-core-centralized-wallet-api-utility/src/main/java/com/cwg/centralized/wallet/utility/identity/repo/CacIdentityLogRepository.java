package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.CacIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**

 @author SmartCore Contributors
 */
public interface CacIdentityLogRepository extends JpaRepository<CacIdentityLog, Long> {

    CacIdentityLog findByWalletNo(String walletNo);

    @Query("select rc from CacIdentityLog rc where rc.rc_number=:rc_number")
    CacIdentityLog findByRcNumber(String rc_number);

    @Query("select rc from CacIdentityLog rc where rc.rc_number = :rc_number AND rc.walletNo = :walletNo")
    CacIdentityLog findByRcNumberAndWalletNo(String rc_number, String walletNo);

    boolean existsCacIdentityLogByWalletNo(String walletNo);

}
