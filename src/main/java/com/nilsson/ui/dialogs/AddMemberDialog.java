package com.nilsson.ui.dialogs;

import com.nilsson.entity.MembershipLevel;
import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Member;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class AddMemberDialog extends Dialog<Member> {

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final ComboBox<String> levelBox = new ComboBox<>();

    private static final String DEFAULT_LEVEL = "Standard";
    private static final String PREMIUM_LEVEL = "Premium";
    private static final String STUDENT_LEVEL = "Student";

    public AddMemberDialog() {
        setTitle(LanguageManager.getInstance().getString("txt.addMemberTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.addMemberHeader"));

        // Apply theme and mouse-drag
        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
        });

        ButtonType addButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.add"),
                ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Form Layout
        GridPane grid = createGridPane();

        // ComboBox
        levelBox.getItems().addAll(DEFAULT_LEVEL, PREMIUM_LEVEL, STUDENT_LEVEL);
        levelBox.setValue(DEFAULT_LEVEL);
        levelBox.setMaxWidth(Double.MAX_VALUE);

        // Grid layout
        grid.add(new Label(LanguageManager.getInstance().getString("table.firstName")), 0, 0);
        grid.add(firstNameField, 1, 0);
        firstNameField.setPromptText(LanguageManager.getInstance().getString("txt.firstNamePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.lastName")), 0, 1);
        grid.add(lastNameField, 1, 1);
        lastNameField.setPromptText(LanguageManager.getInstance().getString("txt.lastNamePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.membershiplevel")), 0, 2);
        grid.add(levelBox, 1, 2);

        // Membership level descriptions
        Label descriptionLabel = new Label(LanguageManager.getInstance().getString("txt.membershipDescription"));

        // Text wrapping and width
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);

        // Styling to look smaller
        descriptionLabel.setStyle("-fx-font-size: 0.85em; -fx-opacity: 0.7;");

        grid.add(descriptionLabel, 1, 3);

        getDialogPane().setContent(grid);

        // Focus
        Platform.runLater(firstNameField::requestFocus);

        // Enable/disable button
        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.disableProperty().bind(
                firstNameField.textProperty().isEmpty()
                        .or(lastNameField.textProperty().isEmpty())
        );

        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String levelString = levelBox.getValue().toUpperCase();
                MembershipLevel level = MembershipLevel.valueOf(levelString);
                return new Member(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        level
                );
            }
            // If Cancel is clicked
            return null;
        });
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(20,20,10,10));
        return grid;
    }
}