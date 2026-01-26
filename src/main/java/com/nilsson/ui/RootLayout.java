package com.nilsson.ui;

import com.nilsson.ui.views.HomeView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The main container for the application.
 */
public class RootLayout extends BorderPane {

    private final Runnable onLanguageChange;

    // Theme Constants
    private static final String DARK_THEME_CSS = "/dark-theme.css";
    private static final String LIGHT_THEME_CSS = "/light-theme.css";

    // Static variable to track the currently active theme URL
    private static String currentThemeUrl;

    public static String getCurrentThemeUrl() {
        return currentThemeUrl;
    }

    private boolean isDarkTheme = true;
    private final Stage stage;
    private final Runnable onLogout;
    private final ServiceContainer services;

    // Constructor
    public RootLayout(Stage stage, Runnable onLogout, CustomTitleBar titleBar,
                      Runnable onLanguageChange, ServiceContainer services) {

        this.stage = stage;
        this.onLogout = onLogout;
        this.onLanguageChange = onLanguageChange;
        this.services = services;

        // Apply the CSS class
        this.getStyleClass().add("root-layout");

        // Initialize theme (default Dark)
        currentThemeUrl = getClass().getResource(DARK_THEME_CSS).toExternalForm();

        // Initialize Side Navigation
        refreshSideNavigation();
        setContent(new HomeView(services));
    }

    public void refreshSideNavigation() {
        SideNavigation sideNav = new SideNavigation(
                this,
                this.stage,
                this.onLogout,
                this.onLanguageChange,
                this.services
        );

        this.setLeft(sideNav);
        sideNav.setPrefWidth(250);
    }

    public void setContent(Node view) {
        this.setCenter(view);
    }

    public boolean toggleTheme() {
        if (this.getScene() == null) {
            System.err.println("Cannot toggle theme: Scene is null.");
            return isDarkTheme;
        }

        String oldTheme;
        String newTheme;

        if (isDarkTheme) {
            oldTheme = getClass().getResource(DARK_THEME_CSS).toExternalForm();
            newTheme = getClass().getResource(LIGHT_THEME_CSS).toExternalForm();
            isDarkTheme = false;
        } else {
            oldTheme = getClass().getResource(LIGHT_THEME_CSS).toExternalForm();
            newTheme = getClass().getResource(DARK_THEME_CSS).toExternalForm();
            isDarkTheme = true;
        }

        this.getScene().getStylesheets().remove(oldTheme);
        if (!this.getScene().getStylesheets().contains(newTheme)) {
            this.getScene().getStylesheets().add(newTheme);
        }
        currentThemeUrl = newTheme;
        return isDarkTheme;
    }
}