/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import com.smart.core.centralized.wallet.generalledger.v2.enumm.PostingModeV2;
import java.util.List;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchLedgerPostRequestV2 {
    private String batchRef;                 // idempotency key
    private String productCode;              // tenant/caller
    private String productName;              // optional
    private String narration;
    private PostingModeV2 postingMode;       // ONE_SIDED or BALANCED
    private List<BatchLegV2> legs;
}
