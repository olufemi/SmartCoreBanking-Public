package com.cwg.centralized.wallet.sessionmanager.requests;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AccountCompleteApiRequest {
	private String userId;
	
	private String password;
	
	private String action;

	private String originFlag;
}
