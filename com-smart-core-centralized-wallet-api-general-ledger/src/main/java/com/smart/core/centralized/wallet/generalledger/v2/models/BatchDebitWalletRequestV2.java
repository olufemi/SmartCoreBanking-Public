/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

/**
 *
 * @author SmartCore Contributors
 */
import com.smart.core.centralized.wallet.generalledger.models.RequestDebitWallet;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class BatchDebitWalletRequestV2 {

    /**
     * A single reference for the whole batch (idempotency for batch).
     * If client retries, same groupRef should return same result.
     */
    @NotNull
    private String groupRef;

    /**
     * If true => fail entire batch if any line fails validation/posting.
     * If false => process each line independently (partial success).
     */
    private boolean allOrNothing = true;

    @Valid
    @NotNull
    private List<RequestDebitWallet> items = new ArrayList<>();
}

