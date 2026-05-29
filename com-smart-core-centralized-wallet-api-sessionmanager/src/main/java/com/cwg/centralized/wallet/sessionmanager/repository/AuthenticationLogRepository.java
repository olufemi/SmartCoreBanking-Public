package com.cwg.centralized.wallet.sessionmanager.repository;

import com.cwg.centralized.wallet.sessionmanager.entities.SessionServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface AuthenticationLogRepository extends JpaRepository<SessionServiceLog, Long>, JpaSpecificationExecutor<SessionServiceLog> {

}
