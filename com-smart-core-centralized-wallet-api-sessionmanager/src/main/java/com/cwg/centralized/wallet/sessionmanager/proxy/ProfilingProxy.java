/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.sessionmanager.proxy;


import org.springframework.cloud.openfeign.FeignClient;


/**
 *
 * @author SmartCore Contributors
 */
@FeignClient(name = "profiling-service")
public interface ProfilingProxy {

  
}
