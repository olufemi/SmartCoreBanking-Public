/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SmartCore Contributors
 */
public class BatchLedgerCreditPostRequestV2 {
     /**
     * If true: ANY failure rolls back the whole batch (atomic).
     * If false: we post what we can; failures returned per line.
     */
    private boolean allOrNothing = true;

    /**
     * Optional: groups items (useful for idempotency grouping & tracing).
     * If you choose to enforce idempotency as (groupRef, requestRef), this becomes important.
     */
    private String groupRef;

    /**
     * Required: each item must have requestRef (idempotency key).
     * For debit: finalCharges should equal amount + fees (validate in API before engine).
     * For credit: amount should represent amount that hits wallet (i.e., net credit), if that is your model.
     */
    private List<SingleLedgerPostRequestV2> items = new ArrayList<>();
}
