/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.models;

import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class ValidateBvnForTier2 {
    private int otp;
    private String requestId;
    private String phoneNumber;
}
