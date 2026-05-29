/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.ProcessorUserFailedTransInfo;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface ProcessorUserFailedTransInfoRepo  extends
        CrudRepository<ProcessorUserFailedTransInfo, String>{
    
}
