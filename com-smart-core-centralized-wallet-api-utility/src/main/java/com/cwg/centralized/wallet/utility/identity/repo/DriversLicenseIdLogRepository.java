package com.cwg.centralized.wallet.utility.identity.repo;


import com.cwg.centralized.wallet.utility.identity.domain.DriversLicenseIdentityLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface DriversLicenseIdLogRepository extends JpaRepository<DriversLicenseIdentityLog, Long> {

    DriversLicenseIdentityLog findByWalletNo(String walletNo);

    boolean existsDriversLicenseIdentityLogByWalletNo(String walletNo);

}
