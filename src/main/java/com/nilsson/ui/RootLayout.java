package com.nilsson.ui;

import com.nilsson.ui.views.HomeView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The main container for the application.
 * Uses a BorderPane to hold the custom title bar (Top), the persistent side navigation (Left)
 * and the dynamic content (Center).
 */
public class RootLayout extends BorderPane {

    private final Runnable onLanguageChange;

    // Theme Constants
    private static final String DARK_THEME_CSS = "/dark-theme.css";
    private static final String LIGHT_THEME_CSS = "/light-theme.css";

    // Static variable to track the currently active theme URL
    private static String currentThemeUrl;

    // Public getter to allow UIUtil to apply the active theme to new windows (like Dialogs/Alerts).
    public static String getCurrentThemeUrl() {
        return currentThemeUrl;
    }

    // Instance variable to track the theme state
    private boolean isDarkTheme = true;

    // Store necessary dependencies to recreate SideNavigation
    private final Stage stage;
    private final Runnable onLogout;

    // The Root Layout
    public RootLayout(Stage stage, Runnable onLogout, CustomTitleBar titleBar, Runnable onLanguageChange) {
        this.stage = stage;
        this.onLogout = onLogout;
        this.onLanguageChange = onLanguageChange;

        // Apply the CSS class
        this.getStyleClass().add("root-layout");

        // Initialize the global theme tracker, start in Dark-mode
        currentThemeUrl = getClass().getResource(DARK_THEME_CSS).toExternalForm();

        // Initialize Side Navigation and set default content
        refreshSideNavigation();
        setContent(new HomeView());
    }

    /**
     * Creates a new SideNavigation instance and sets it to the left of the BorderPane.
     * This method must be called after a successful login to ensure the username is updated.
     */
    public void refreshSideNavigation() {
        // Side Navigation (Left)
        SideNavigation sideNav = new SideNavigation(this, this.stage, this.onLogout, this.onLanguageChange);
        this.setLeft(sideNav);

        // Set side navigation width
        sideNav.setPrefWidth(250);
    }

    // Public method to swap the main content view.
    public void setContent(Node view) {
        this.setCenter(view);
    }

    // Toggles the application stylesheet between dark-theme.css and light-theme.css.
    // Updates the currentThemeUrl and swaps the stylesheet on the Scene.
    public boolean toggleTheme() {
        if (this.getScene() == null) {
            System.err.println("Cannot toggle theme: Scene is null.");
            return isDarkTheme;
        }

        String oldTheme;
        String newTheme;

        if (isDarkTheme) {
            // Switching from Dark to Light
            oldTheme = getClass().getResource(DARK_THEME_CSS).toExternalForm();
            newTheme = getClass().getResource(LIGHT_THEME_CSS).toExternalForm();
            isDarkTheme = false;
        } else {
            // Switching from Light to Dark
            oldTheme = getClass().getResource(LIGHT_THEME_CSS).toExternalForm();
            newTheme = getClass().getResource(DARK_THEME_CSS).toExternalForm();
            isDarkTheme = true;
        }

        // Remove the old stylesheet
        this.getScene().getStylesheets().remove(oldTheme);

        // Add the new stylesheet
        if (!this.getScene().getStylesheets().contains(newTheme)) {
            this.getScene().getStylesheets().add(newTheme);
        }

        // Update the tracker for dialogs/alerts
        currentThemeUrl = newTheme;

        return isDarkTheme;
    }
}