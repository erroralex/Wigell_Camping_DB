package com.nilsson.ui;

import com.nilsson.util.LanguageManager;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Utility class for common UI operations, such as displaying standard alerts
 * and error messages, especially replacing the use of system alerts.
 */
public class UIUtil {

    private static double xOffset = 0;
    private static double yOffset = 0;

    /**
     * Applies the custom theme (retrieved from RootLayout), removes the title bar (undecorated style),
     * and adds drag functionality to any JavaFX Dialog or Alert.
     *
     * @param dialog The Dialog object to modify.
     */
    public static void applyDialogSetup(Dialog<?> dialog) {
        try {
            DialogPane dialogPane = dialog.getDialogPane();

            // Set Stage to UNDECORATED
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.initStyle(StageStyle.UNDECORATED);

            // Apply the currently active CSS theme from the RootLayout
            String cssUrl = RootLayout.getCurrentThemeUrl();
            if (cssUrl != null) {
                if (!dialogPane.getStylesheets().contains(cssUrl)) {
                    dialogPane.getStylesheets().add(cssUrl);
                }
            }

            // Apply custom style class
            dialogPane.getStyleClass().add("add-entity-dialog");

            // Add Drag Functionality
            applyDragListeners(dialogPane, stage);

        } catch (Exception e) {
            System.err.println("Could not load CSS stylesheet or apply dialog setup.");
            e.printStackTrace();
        }
    }

    private static void applyDragListeners(Node root, Stage stage) {
        // Store the initial position when the mouse is pressed
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Move the stage when the mouse is dragged
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    // Helper method to apply the currently active theme to any Alert dialog.
    private static void applyTheme(Alert alert) {
        applyDialogSetup(alert);
    }

    /**
     * Shows a standard error alert dialog to the user.
     *
     * @param title   The title of the alert window.
     * @param header  The header text (main message).
     * @param content The detailed content message.
     */
    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        applyTheme(alert);

        alert.showAndWait();
    }

    /**
     * Shows an alert dialog containing the full stack trace of an exception.
     *
     * @param title     The title of the alert window.
     * @param header    The header text.
     * @param throwable The exception or throwable to display.
     */
    public static void showExceptionAlert(String title, String header, Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(throwable.getMessage());

        applyTheme(alert);

        // Create expandable Exception details
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        Label label = new Label(LanguageManager.getInstance().getString("txt.stacktrace"));
        label.getStyleClass().add("label");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.getStyleClass().addAll("text-field", "text-area");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable content into the dialog
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    /**
     * Shows standard information alert dialog.
     *
     * @param title   The title of the alert window.
     * @param header  The header text.
     * @param content The detailed content message.
     */
    public static void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        applyTheme(alert);

        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog with OK and Cancel options.
     *
     * @param title   The title of the alert window.
     * @param header  The header text (main message).
     * @param content The detailed content message.
     * @return true if the user clicks OK, false if the user clicks Cancel or closes the dialog.
     */
    public static boolean showConfirmationAlert(String title, String header, String content) {
        // Create an alert with the Confirmation type
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        applyTheme(alert);

        // Show the alert and wait for a response
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the result matches the OK button
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Creates an HBox layout that pushes the main graphic and the toggle icon
     * to the opposite sides of the button text space.
     *
     * @param mainGraphic   The main icon.
     * @param toggleGraphic The indicator icon.
     * @return An HBox containing the graphics with a spacer in between.
     */
    public static HBox createIconWithSpacer(Node mainGraphic, Node toggleGraphic) {
        // Spacer that consumes all available space
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Container holding the main icon, the spacer, and the toggle icon
        HBox container = new HBox(mainGraphic, spacer, toggleGraphic);

        container.setMaxWidth(Double.MAX_VALUE);
        container.setSpacing(5);

        return container;
    }
}