package com.cwg.centralized.wallet.utility.identity.exceptions;

public class InternalServerException extends RuntimeException {

    private static final long serialVersionUID = 19371L;

    public InternalServerException() {
        super();
    }

    public InternalServerException(String message) {
        super(message);
    }
}
