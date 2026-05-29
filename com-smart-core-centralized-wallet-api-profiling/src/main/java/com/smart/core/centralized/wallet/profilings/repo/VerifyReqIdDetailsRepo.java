/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.VerifyReqIdDetails;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface VerifyReqIdDetailsRepo extends
        CrudRepository<VerifyReqIdDetails, String> {

    @Query("select ud from VerifyReqIdDetails ud where ud.requestId=:requestId")
    List<VerifyReqIdDetails> findByRequestId(String requestId);

    @Query("select ud from VerifyReqIdDetails ud where ud.processId=:processId")
    List<VerifyReqIdDetails> findByProcessId(String processId);

    @Query("select bs from VerifyReqIdDetails bs where bs.requestId=:requestId")
    VerifyReqIdDetails findByRequestIdList(String requestId);

    boolean existsByRequestId(String requestId);

}
