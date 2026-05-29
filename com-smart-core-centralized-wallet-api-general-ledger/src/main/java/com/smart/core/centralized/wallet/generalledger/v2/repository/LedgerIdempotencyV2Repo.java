package com.smart.core.centralized.wallet.generalledger.v2.repository;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerIdempotencyV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerIdempotencyV2Repo extends JpaRepository<LedgerIdempotencyV2, String> {
}
