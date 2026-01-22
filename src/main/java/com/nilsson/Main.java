package com.nilsson;

import com.nilsson.jdbcApp.service.SessionTimerService;
import com.nilsson.jdbcApp.ui.CustomTitleBar;
import com.nilsson.jdbcApp.ui.ResizeHelper; // <--- ADD THIS IMPORT
import com.nilsson.jdbcApp.ui.RootLayout;
import com.nilsson.jdbcApp.ui.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    private SessionTimerService sessionTimerService;
    private RootLayout rootLayout;
    private CustomTitleBar customTitleBar;
    private Runnable onLogout;
    private Runnable onLanguageChange;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Removes Standard Title Bar
            primaryStage.initStyle(StageStyle.UNDECORATED);

            // Custom Title Bar
            customTitleBar = new CustomTitleBar(primaryStage, this::handleCloseOrLogout);

            // Instantiate Session Timer and Static UserSession
            sessionTimerService = new SessionTimerService(customTitleBar);
            UserSession.initialize(customTitleBar);

            // On Logout:
            onLogout = () -> {
                UserSession.logout();
                showLoginView(primaryStage);
            };

            // On Language change:
            onLanguageChange = () -> {
                rootLayout = new RootLayout(primaryStage, onLogout, customTitleBar, onLanguageChange);

                BorderPane contentWrapper = new BorderPane();
                contentWrapper.setTop(customTitleBar);
                contentWrapper.setCenter(rootLayout);
                primaryStage.getScene().setRoot(contentWrapper);
            };

            // Instantiate the single RootLayout
            rootLayout = new RootLayout(primaryStage, onLogout, customTitleBar, onLanguageChange);

            // Show the initial login view
            showLoginView(primaryStage);

            // Initial Scene Setup
            Scene scene = primaryStage.getScene();

            // CSS for styling
            String cssPath = getClass().getResource("/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            // --- ADD RESIZE LISTENER HERE ---
            ResizeHelper.addResizeListener(primaryStage);
            // --------------------------------

            // Set the stage properties and show the application.
            primaryStage.setTitle("Wigell Camping - Login");
            primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("/icon.png").toExternalForm()));

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading resources or starting application:");
            e.printStackTrace();
        }
    }

    // ... (Rest of the class remains the same)

    // Handles the clean shutdown logic when pressing the 'X' button.
    private void handleCloseOrLogout() {
        // Stop the Timer Thread
        if (sessionTimerService != null) {
            sessionTimerService.stop();
        }
        // Log out the user
        UserSession.logout();
        // Close the application
        if (getPrimaryStage() != null) {
            getPrimaryStage().close();
        }
    }

    private void showLoginView(Stage primaryStage) {
        LoginView newLoginView = new LoginView(primaryStage, rootLayout);

        BorderPane loginWrapper = new BorderPane();
        loginWrapper.setTop(customTitleBar);
        loginWrapper.setCenter(newLoginView);
        loginWrapper.getStyleClass().add("login-wrapper");

        if (primaryStage.getScene() != null) {
            primaryStage.getScene().setRoot(loginWrapper);
        } else {
            primaryStage.setScene(new Scene(loginWrapper, WIDTH, HEIGHT));
        }

        primaryStage.setTitle("Wigell Camping - Login");
    }

    private Stage getPrimaryStage() {
        return (Stage) customTitleBar.getScene().getWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}