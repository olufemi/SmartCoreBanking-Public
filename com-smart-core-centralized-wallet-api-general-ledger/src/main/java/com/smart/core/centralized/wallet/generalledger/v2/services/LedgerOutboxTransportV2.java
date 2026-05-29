package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;

public interface LedgerOutboxTransportV2 {

    void publish(LedgerOutboxEventV2 event);
}
