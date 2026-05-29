package com.cwg.centralized.wallet.utility.models;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OtpValidateRequest1 {
	
	@NotNull(message = "RequestId can't be null")
	private String requestId;
	
	@NotNull(message = "Otp can't be null")
	private Integer otp;
	
}
