/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.models;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class ApiResponseModel implements Serializable {

    private int statusCode;
    private String description;
    private Object data;
    private String other;

    private String externalRefrence;
    private String benefNarration;
    private String status;
    private String amount;
    private String fees;
    private String requestedAmount;

}
