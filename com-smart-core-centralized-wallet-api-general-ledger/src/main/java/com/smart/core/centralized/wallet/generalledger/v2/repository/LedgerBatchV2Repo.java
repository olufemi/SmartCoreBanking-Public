/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.repository;

/**
 *
 * @author SmartCore Contributors
 */


import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerBatchV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerBatchV2Repo extends JpaRepository<LedgerBatchV2, String> {
}

