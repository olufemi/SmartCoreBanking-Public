package com.smart.core.centralized.wallet.generalledger.v2.repository;

import java.math.BigDecimal;

public interface LedgerPortalProductProjection {
    String getProductCode();
    Long getTxnCount();
    BigDecimal getTotalValue();
}
