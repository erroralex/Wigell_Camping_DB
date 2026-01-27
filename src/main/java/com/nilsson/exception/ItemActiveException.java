package com.nilsson.exception;

public class ItemActiveException extends RuntimeException {
    public ItemActiveException(String message) {
        super(message);
    }
}
