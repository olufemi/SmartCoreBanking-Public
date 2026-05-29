/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.model;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class InitiateForgetPwdDataUser {

    @ApiModelProperty(notes = "The Emailaddress")
    @NotNull(message = "the field \"Emailaddress\" is not nillable")
    @NotBlank
    @Expose
    private String emailAddress;
}
