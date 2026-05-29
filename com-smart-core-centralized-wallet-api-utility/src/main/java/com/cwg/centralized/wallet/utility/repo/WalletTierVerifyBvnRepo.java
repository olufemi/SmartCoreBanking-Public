/*
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates
 and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.repo;

import com.cwg.centralized.wallet.utility.domain.WalletTierVerifyBvn;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface WalletTierVerifyBvnRepo extends CrudRepository<WalletTierVerifyBvn, String> {

    @Query("select ud from WalletTierVerifyBvn ud where ud.walletNo=:walletNo")
    List<WalletTierVerifyBvn> findByWalletNo(String walletNo);

    @Query("select bs from WalletTierVerifyBvn bs where bs.walletNo=:walletNo")
    WalletTierVerifyBvn findByWalletNoDe(String walletNo);

    @Query("SELECT u FROM WalletTierVerifyBvn u where u.phoneNumberProductCode = :phoneNumberProductCode")
    List<WalletTierVerifyBvn> findByPhoneNumberProductCode(String phoneNumberProductCode);
    
      @Query("SELECT u FROM WalletTierVerifyBvn u where u.phoneNumberProductCode = :phoneNumberProductCode")
    WalletTierVerifyBvn findByPhoneNumberProductCodeDe(String phoneNumberProductCode);

}
