/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.repo;

import com.cwg.centralized.wallet.utility.domain.BvnNumberLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author SmartCore Contributors
 */
public interface BvnNumberLogRepo extends JpaRepository<BvnNumberLog, Long> {

    @Query("select bs from BvnNumberLog bs where bs.id=:id")
    BvnNumberLog findByBvnLogId(Long id);

    @Query("select bs from BvnNumberLog bs where bs.requestId=:requestId")
    List<BvnNumberLog> findByRequestId(String requestId);

    @Query("select ud from BvnNumberLog ud where ud.bvn=:bvn")
    List<BvnNumberLog> findByBvn(String bvn);
}
