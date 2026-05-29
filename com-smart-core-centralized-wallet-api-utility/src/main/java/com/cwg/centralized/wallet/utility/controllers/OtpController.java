package com.cwg.centralized.wallet.utility.controllers;


import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.OtpRequest;
import com.cwg.centralized.wallet.utility.models.OtpValidateRequest;
import com.cwg.centralized.wallet.utility.models.ReqRequestId;
import com.cwg.centralized.wallet.utility.services.OtpService;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private static final Logger LOG = LoggerFactory.getLogger(OtpController.class);

    private final OtpService otpService;

   
    @PostMapping("/send-email")
    public ResponseEntity<BaseResponse> sendOtpEmail(@RequestBody @Valid OtpRequest request) {
        BaseResponse baseResponse = otpService.createAndSendOtpEmail(request);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<BaseResponse> validateOtp(@RequestBody @Valid OtpValidateRequest request) {
        BaseResponse baseResponse = otpService.validateOtp(request);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/request")
    public ResponseEntity<BaseResponse> getOtp(@RequestBody ReqRequestId requestId) {
        BaseResponse baseResponse = otpService.getOtpByRequestIdExist(requestId);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createOtp(@RequestBody @Valid OtpRequest request) {
        BaseResponse baseResponse = otpService.createOtp(request);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
