/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.sessionmanager.requests;

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
public class AuthUserRequest {

    @ApiModelProperty(notes = "The Emailaddress")
    @NotNull(message = "the field \"Emailaddress\" is not nillable")
    @NotBlank
    @Expose
    private String emailAddress;
    @ApiModelProperty(notes = "The password")
    @NotNull(message = "the field \"password\" is not nillable")
    @NotBlank
    @Expose
    private String password;

}
