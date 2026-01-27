package com.nilsson.exception;

public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String message, Exception e) {
        super(message);
    }
}
