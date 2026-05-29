package com.cwg.centralized.wallet.utility.identity.exceptions;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 19371L;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }
}
