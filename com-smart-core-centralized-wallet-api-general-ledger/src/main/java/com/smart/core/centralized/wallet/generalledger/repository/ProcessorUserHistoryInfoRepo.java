/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.ProcessorHistoryInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author SmartCore Contributors
 */
@Repository
public interface ProcessorUserHistoryInfoRepo extends
        CrudRepository<ProcessorHistoryInfo, String>{
    
}
