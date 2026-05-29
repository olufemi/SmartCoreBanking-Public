/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.proxy;

import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.model.OtpRequest;
import com.smart.core.centralized.wallet.profilings.model.OtpValidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author SmartCore Contributors
 */
@FeignClient(name = "utilities-service")
public interface UtilitiesProxy {

    @RequestMapping(value = "/otp/send-email", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse sendOtpEmail(@RequestBody OtpRequest rq);

    @RequestMapping(value = "/otp/validate", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse validateOtp(@RequestBody OtpValidateRequest rq);

}
