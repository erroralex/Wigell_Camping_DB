package com.nilsson.service;

import com.nilsson.util.UserSession;

public class AuthService {

    /**
     * Validates the login credentials.
     * Currently, hardcoded to accept password "0000".
     *
     * @param username The username provided (not strictly checked in this version).
     * @param password The password provided.
     * @return true if credentials are valid.
     */
    public boolean login(String username, String password) {
        return "0000".equals(password);
    }
}
