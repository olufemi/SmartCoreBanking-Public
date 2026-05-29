/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.sessionmanager.proxy;

import com.cwg.centralized.wallet.sessionmanager.requests.AuthUserRequest;
import com.cwg.centralized.wallet.sessionmanager.requests.EmailRequestDemo;
import com.cwg.centralized.wallet.sessionmanager.responses.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author SmartCore Contributors
 */
@FeignClient(name = "utility-service", fallback = UtilityServiceFeignService.UtilityServiceProxyImpl.class)
public interface UtilityServiceFeignService {

    @RequestMapping(value = "/email/send", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse sendUserEmailAndSms(@RequestBody EmailRequestDemo rq);

    @RequestMapping(value = "/utilities/usermgt/user", consumes = "application/json", method = RequestMethod.POST)
    public BaseResponse authenticateUser(@RequestBody AuthUserRequest rq, @RequestHeader("channel") String channel);

    class UtilityServiceProxyImpl implements UtilityServiceFeignService {

        @Override
        public BaseResponse sendUserEmailAndSms(EmailRequestDemo rq) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BaseResponse authenticateUser(AuthUserRequest rq, String channel) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
