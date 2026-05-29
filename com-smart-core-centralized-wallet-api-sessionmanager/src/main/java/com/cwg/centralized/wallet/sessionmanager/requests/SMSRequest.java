package com.cwg.centralized.wallet.sessionmanager.requests;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SMSRequest {

    @NotNull(message = "ReceiverNumber can't be null")
    private String receiverNumber;

    @NotNull(message = "SMSMessage can't be null")
    private String smsMessage;

    @NotNull(message = "ApplicationId can't be null")
    private String applicationId;

    @NotNull(message = "Priority can't be null")
    private String priority;

    @Override
    public String toString() {
        return "{ \"ReceiverNumber\":\"" + receiverNumber + "\","
                + "\"SMSMessage\":\"" + smsMessage + "\","
                + " \"ApplicationId\":\"" + applicationId + "\","
                + "\"Priority\":\"" + priority + "\"" + "}";
    }

}
