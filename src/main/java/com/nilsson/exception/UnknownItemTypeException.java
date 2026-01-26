package com.nilsson.exception;

public class UnknownItemTypeException extends RuntimeException {
    public UnknownItemTypeException(String message) {
        super(message);
    }
}
