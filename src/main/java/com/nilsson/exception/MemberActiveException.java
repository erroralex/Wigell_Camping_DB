package com.nilsson.exception;

public class MemberActiveException extends RuntimeException {
    public MemberActiveException(String message) {
        super(message);
    }
}
