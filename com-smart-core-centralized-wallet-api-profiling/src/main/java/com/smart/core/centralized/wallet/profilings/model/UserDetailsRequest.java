/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class UserDetailsRequest {

    @NotNull(message = "productName can't be empty")
    private String productName;
    @NotNull(message = "password can't be empty")
    private String password;
    @NotNull(message = "confPassword can't be empty")
    private String confPassword;
    @NotNull(message = "emailAddress can't be empty")
    private String emailAddress;
    @NotNull(message = "clearanceId can't be empty")
    private String clearanceId;

}
