package com.cwg.centralized.wallet.utility.identity.repo;

import com.cwg.centralized.wallet.utility.identity.domain.NipIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface NipIdLogRepository extends JpaRepository<NipIdentityLog, Long> {

    NipIdentityLog findByWalletNo(String walletNo);

    boolean existsNipIdentityLogByWalletNo(String walletNo);
}
