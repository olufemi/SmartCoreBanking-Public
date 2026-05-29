package com.cwg.centralized.wallet.sessionmanager.requests;


import lombok.Data;

@Data
public class OtpValidateRequest {
	
	private String requestId;
	
	private String uuid;
	
	private Integer otp;
}
