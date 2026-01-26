package com.nilsson.ui.views;

import com.nilsson.service.AuthService;
import com.nilsson.util.LanguageManager;
import com.nilsson.util.UserSession;
import com.nilsson.ui.CustomTitleBar;
import com.nilsson.ui.RootLayout;
import com.nilsson.ui.UIUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * The initial screen of the application, used for user authentication.
 * It will swap the main content to RootLayout when successfully logged in.
 */
public class LoginView extends VBox {

    private final Stage primaryStage;
    private final AuthService authService;

    public LoginView(Stage primaryStage, RootLayout rootLayout, AuthService authService) {
        this.primaryStage = primaryStage;
        this.authService = authService;

        // Layout Setup for centered content
        VBox contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setSpacing(20);
        contentVBox.setPadding(new Insets(50));
        contentVBox.getStyleClass().add("login-view");

        // Main Logo image Loading with error handling
        Label logo = new Label();
        Image logoImage = null;
        try {
            // Load the logo image resource
            logoImage = new Image(getClass().getResource("/logo.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading status image: " + "/logo.png" + ". Using text placeholder.");
        }

        if (logoImage != null) {
            ImageView logoImageView = new ImageView(logoImage);
            logoImageView.setFitWidth(800);
            logoImageView.setPreserveRatio(true);
            logo.setGraphic(logoImageView);
        } else {
            // Fallback text if image fails to load
            logo.setText(LanguageManager.getInstance().getString("app.title"));
        }

        // Text Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText(LanguageManager.getInstance().getString("prompt.username"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(LanguageManager.getInstance().getString("prompt.password"));

        // Wrap inputs with icons in containers
        HBox usernameContainer = createInputContainer(usernameField, FontAwesome.USER);
        HBox passwordContainer = createInputContainer(passwordField, FontAwesome.LOCK);

        // Login Button
        Button loginButton = new Button(LanguageManager.getInstance().getString("btn.login"));
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(150);
        loginButton.setDefaultButton(true);
        loginButton.setGraphic(new FontIcon(FontAwesome.SIGN_IN));

        // Exit Button
        Button exitButton = new Button(LanguageManager.getInstance().getString("btn.exit"));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setMaxWidth(150);
        exitButton.setOnAction(e -> {
            // Perform UserSession cleanup before closing
            UserSession.logout();
            primaryStage.close();
        });
        exitButton.setGraphic(new FontIcon(FontAwesome.POWER_OFF));

        // Add components to the VBox
        contentVBox.getChildren().addAll(logo, usernameContainer, passwordContainer, loginButton, exitButton);

        // Corner Image Component
        ImageView cornerImageView = new ImageView();
        Image cornerImage = null;
        try {
            cornerImage = new Image(getClass().getResource("/corner_logo.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading corner image: " + e.getMessage() + ". Check if '/corner_logo.png' exists.");
        }

        if (cornerImage != null) {
            cornerImageView.setImage(cornerImage);
            cornerImageView.setFitWidth(70);
            cornerImageView.setPreserveRatio(true);
        }

        // Container Setup for Positioning
        AnchorPane rootAnchorPane = new AnchorPane();
        rootAnchorPane.getChildren().add(contentVBox);

        AnchorPane.setTopAnchor(contentVBox, 0.0);
        AnchorPane.setBottomAnchor(contentVBox, 0.0);
        AnchorPane.setLeftAnchor(contentVBox, 0.0);
        AnchorPane.setRightAnchor(contentVBox, 0.0);

        // Add the corner image and anchor it to the bottom right
        if (cornerImage != null) {
            rootAnchorPane.getChildren().add(cornerImageView);

            AnchorPane.setBottomAnchor(cornerImageView, 40.0);
            AnchorPane.setRightAnchor(cornerImageView, 40.0);
        }

        this.getChildren().add(rootAnchorPane);
        VBox.setVgrow(rootAnchorPane, Priority.ALWAYS);


        // Login Action Handling
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            // Validate using AuthService
            if (authService.login(username, password)) {

                // Login successful - Proceed with Session and UI swap
                UserSession.login(username.isEmpty() ? "Guest" : username);

                if (rootLayout != null) {
                    rootLayout.refreshSideNavigation();

                    CustomTitleBar sharedTitleBar = UserSession.getInitializedTitleBar();
                    if (rootLayout.getTop() == null && sharedTitleBar != null) {
                        rootLayout.setTop(sharedTitleBar);
                    }

                    primaryStage.getScene().setRoot(rootLayout);
                    primaryStage.setTitle(LanguageManager.getInstance().getString("app.title"));
                }
            } else {
                UIUtil.showErrorAlert(
                        LanguageManager.getInstance().getString("error.loginFailedTitle"),
                        null,
                        LanguageManager.getInstance().getString("error.loginFailedMsg")
                );
            }
        });
    }

    /**
     * Helper method to wrap a TextField/PasswordField and a FontIcon inside an HBox.
     *
     * @param field    The input control.
     * @param iconCode The Ikonli icon code.
     * @return An HBox containing the icon and the input field.
     */
    private HBox createInputContainer(TextField field, Ikon iconCode) {
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(20);

        icon.setStyle("-fx-font-family: 'FontAwesome';");

        // Create HBox
        HBox container = new HBox(6, icon, field);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(300);
        container.setPadding(new Insets(5, 0, 5, 0));

        HBox.setHgrow(field, Priority.ALWAYS);

        field.setMaxWidth(Double.MAX_VALUE);

        return container;
    }
}