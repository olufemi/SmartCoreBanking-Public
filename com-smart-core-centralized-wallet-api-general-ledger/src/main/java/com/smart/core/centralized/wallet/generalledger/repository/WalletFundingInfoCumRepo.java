/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.WalletFundingInfoCum;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface WalletFundingInfoCumRepo extends
        CrudRepository<WalletFundingInfoCum, String> {

    Optional<WalletFundingInfoCum> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
