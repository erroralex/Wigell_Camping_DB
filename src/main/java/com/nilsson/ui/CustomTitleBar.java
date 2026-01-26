package com.nilsson.ui;

import com.nilsson.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Custom title bar component for an undecorated Stage.
 * Provides window dragging, snapping, maximizing, and control buttons.
 */
public class CustomTitleBar extends HBox {

    private double xOffset = 0;
    private double yOffset = 0;

    // Sensitivity for snapping (how close to the edge you must drag)
    private static final double SNAP_THRESHOLD = 20.0;

    // Timer components
    public Label timeDisplayLabel = new Label("00:00:00");
    private final Label prefixLabel = new Label(LanguageManager.getInstance().getString("header.sessionTimer") + " ");
    private final HBox timerContainer;
    private final Runnable onExitCleanup;

    public CustomTitleBar(Stage primaryStage, Runnable onExitCleanup) {
        this.onExitCleanup = onExitCleanup;

        // Apply CSS class
        this.getStyleClass().add("custom-title-bar");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(40); // Ensure enough height for hit targets

        // Application Title (Aligned Left)
        Label titleLabel = new Label("Wigell Camping Admin Portal");
        titleLabel.getStyleClass().add("title-label");

        // Left Spacer
        HBox leftSpacer = new HBox();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        // Session Timer Container (Aligned Center)
        prefixLabel.getStyleClass().add("timer-prefix");
        timeDisplayLabel.getStyleClass().add("timer");

        timerContainer = new HBox(5, prefixLabel, timeDisplayLabel);
        timerContainer.setAlignment(Pos.CENTER);
        timerContainer.setVisible(false);
        timerContainer.setManaged(false);

        // Right Spacer
        HBox rightSpacer = new HBox();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Window Controls
        Button minimizeBtn = new Button();
        minimizeBtn.setGraphic(new FontIcon(FontAwesome.MINUS));
        minimizeBtn.getStyleClass().add("window-button");
        minimizeBtn.setOnAction(e -> primaryStage.setIconified(true));

        Button maximizeBtn = new Button();
        maximizeBtn.setGraphic(new FontIcon(FontAwesome.WINDOW_MAXIMIZE));
        maximizeBtn.getStyleClass().add("window-button");
        maximizeBtn.setOnAction(e -> toggleMaximize(primaryStage, maximizeBtn));

        Button closeBtn = new Button();
        closeBtn.setGraphic(new FontIcon(FontAwesome.TIMES));
        closeBtn.getStyleClass().addAll("window-button", "window-close");
        closeBtn.setOnAction(e -> {
            if (this.onExitCleanup != null) {
                this.onExitCleanup.run();
            }
            primaryStage.close();
        });

        // Add components: Title | Left Spacer | Timer | Right Spacer | Controls
        this.getChildren().addAll(titleLabel, leftSpacer, timerContainer, rightSpacer, minimizeBtn, maximizeBtn, closeBtn);

        // Dragging Logic
        this.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        this.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                // 1. Handle "Tear-off": If dragging while maximized, restore first
                if (primaryStage.isMaximized()) {
                    double ratioX = event.getSceneX() / primaryStage.getWidth();
                    toggleMaximize(primaryStage, maximizeBtn);
                    // Adjust offset so mouse stays relative to the new window width
                    xOffset = primaryStage.getWidth() * ratioX;
                }

                double newX = event.getScreenX() - xOffset;
                double newY = event.getScreenY() - yOffset;

                // Basic constraint: Prevent title bar from getting lost above screen
                Screen screen = getScreenForCursor(event.getScreenX(), event.getScreenY());
                Rectangle2D bounds = screen.getVisualBounds();

                if (newY < bounds.getMinY()) {
                    newY = bounds.getMinY();
                }

                primaryStage.setX(newX);
                primaryStage.setY(newY);
            }
        });

        // 2. Handle "Snap" on Release
        this.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleSnap(primaryStage, maximizeBtn, event.getScreenX(), event.getScreenY());
            }
        });

        // 3. Double Click to Maximize
        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                toggleMaximize(primaryStage, maximizeBtn);
            }
        });
    }

    // Toggles the visibility of the session timer container.
    public void setTimerVisible(boolean visible) {
        javafx.application.Platform.runLater(() -> {
            timerContainer.setVisible(visible);
            timerContainer.setManaged(visible);
        });
    }

    /**
     * Determines which "zone" the cursor is in and snaps the window accordingly.
     */
    private void handleSnap(Stage stage, Button maxBtn, double cursorX, double cursorY) {
        Screen screen = getScreenForCursor(cursorX, cursorY);
        Rectangle2D bounds = screen.getVisualBounds();

        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();

        boolean nearTop = cursorY <= minY + SNAP_THRESHOLD;
        boolean nearBottom = cursorY >= maxY - SNAP_THRESHOLD;
        boolean nearLeft = cursorX <= minX + SNAP_THRESHOLD;
        boolean nearRight = cursorX >= maxX - SNAP_THRESHOLD;

        // CORNER SNAPPING (Quarter)
        if (nearTop && nearLeft) {
            // Top-Left Quarter
            snapWindow(stage, maxBtn, minX, minY, width / 2, height / 2);
        } else if (nearTop && nearRight) {
            // Top-Right Quarter
            snapWindow(stage, maxBtn, minX + width / 2, minY, width / 2, height / 2);
        } else if (nearBottom && nearLeft) {
            // Bottom-Left Quarter
            snapWindow(stage, maxBtn, minX, minY + height / 2, width / 2, height / 2);
        } else if (nearBottom && nearRight) {
            // Bottom-Right Quarter
            snapWindow(stage, maxBtn, minX + width / 2, minY + height / 2, width / 2, height / 2);
        }

        // EDGE SNAPPING (Half & Maximize)
        else if (nearTop) {
            // Full Screen (Maximize)
            if (!stage.isMaximized()) {
                toggleMaximize(stage, maxBtn);
            }
        } else if (nearLeft) {
            // Left Half
            snapWindow(stage, maxBtn, minX, minY, width / 2, height);
        } else if (nearRight) {
            // Right Half
            snapWindow(stage, maxBtn, minX + width / 2, minY, width / 2, height);
        }
    }

    /**
     * Helper to manually size and position the window, ensuring Maximize state is off.
     */
    private void snapWindow(Stage stage, Button maxBtn, double x, double y, double w, double h) {
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            maxBtn.setGraphic(new FontIcon(FontAwesome.WINDOW_MAXIMIZE));
        }
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(w);
        stage.setHeight(h);
    }

    private void toggleMaximize(Stage stage, Button btn) {
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            btn.setGraphic(new FontIcon(FontAwesome.WINDOW_MAXIMIZE));
        } else {
            stage.setMaximized(true);
            btn.setGraphic(new FontIcon(FontAwesome.WINDOW_RESTORE));
        }
    }

    /**
     * Helper to find the screen containing the cursor.
     */
    private Screen getScreenForCursor(double x, double y) {
        var screens = Screen.getScreensForRectangle(x, y, 1, 1);
        return screens.isEmpty() ? Screen.getPrimary() : screens.get(0);
    }
}