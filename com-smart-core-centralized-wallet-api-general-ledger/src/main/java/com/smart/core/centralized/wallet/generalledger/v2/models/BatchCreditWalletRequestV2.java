/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import com.smart.core.centralized.wallet.generalledger.models.CreditWallet;
import java.util.List;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchCreditWalletRequestV2 {
    private boolean allOrNothing = true;
    private String groupRef;
    private List<CreditWallet> items;
}
