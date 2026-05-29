/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchLedgerFullPostRequestV2 {
    // @NotNull

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean allOrNothing = true;

    @NotNull
    private String groupRef;

    @NotNull
    private List<BatchLedgerItemV2> items;
}
