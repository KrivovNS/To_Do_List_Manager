package com.mipt.To_Do_List_Manager.exception;

public class ExternalRateLimitException extends RuntimeException {
    public ExternalRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
