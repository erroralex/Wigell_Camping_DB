package com.nilsson.util;

import com.nilsson.service.SessionTimerService;
import com.nilsson.ui.CustomTitleBar;

// Application-wide user session data
public class UserSession {

    private static String currentUsername = "Guest";
    private static boolean isLoggedIn = false;
    private static SessionTimerService sessionTimerService;
    private static CustomTitleBar customTitleBarView;

    // Static initializer method
    public static void initialize(CustomTitleBar view) {
        if (sessionTimerService == null) {
            sessionTimerService = new SessionTimerService(view);
            customTitleBarView = view;
        }
    }

    // Getter for the initialized CustomTitleBar
    public static CustomTitleBar getInitializedTitleBar() {
        return customTitleBarView;
    }

    // Sets the session data upon successful login.
    public static void login(String username) {
        currentUsername = username;
        isLoggedIn = true;
        System.out.println("User " + username + " logged in.");

        // Start the timer in its new dedicated thread
        if (sessionTimerService != null) {
            sessionTimerService.start();
        } else {
            System.err.println("Error: TimerService was not initialized via UserSession.initialize(view).");
        }

        // Show the timer when logged in
        if (customTitleBarView != null) {
            customTitleBarView.setTimerVisible(true);
        }
    }

    // Clears the session data upon logout.
    public static void logout() {
        if (sessionTimerService != null) {
            sessionTimerService.stop();
            sessionTimerService.reset();
        }

        // Hide the timer when logged out
        if (customTitleBarView != null) {
            customTitleBarView.setTimerVisible(false);
        }

        currentUsername = "Guest";
        isLoggedIn = false;
        System.out.println("User logged out.");
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
}