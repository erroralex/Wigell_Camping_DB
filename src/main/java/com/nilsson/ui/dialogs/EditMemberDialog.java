package com.nilsson.ui.dialogs;

import com.nilsson.entity.MembershipLevel;
import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Member;
import com.nilsson.ui.UIUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class EditMemberDialog extends Dialog<Member> {

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final ComboBox<String> levelBox = new ComboBox<>();

    private static final String STANDARD_LEVEL = "Standard";
    private static final String PREMIUM_LEVEL = "Premium";
    private static final String STUDENT_LEVEL = "Student";

    private final Member memberToEdit;

    public EditMemberDialog(Member memberToEdit) {

        this.memberToEdit = memberToEdit;

        setTitle(LanguageManager.getInstance().getString("txt.editMemberTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.editMemberHeader") + memberToEdit.getFirstName() + " " + memberToEdit.getLastName());

        // Apply theme and mouse-drag
        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
        });

        ButtonType saveButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.save"),
                ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType = new ButtonType(LanguageManager.getInstance().getString("btn.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().clear();
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Form Layout
        GridPane grid = createGridPane();;

        // ComboBox
        switch (memberToEdit.getMembershipLevel()) {
            case PREMIUM -> levelBox.setValue(PREMIUM_LEVEL);
            case STUDENT -> levelBox.setValue(STUDENT_LEVEL);
            default -> levelBox.setValue(STANDARD_LEVEL);
        }

        levelBox.setMaxWidth(Double.MAX_VALUE);

        // Grid layout
        grid.add(new Label(LanguageManager.getInstance().getString("table.firstName")), 0, 0);
        grid.add(firstNameField, 1, 0);
        firstNameField.setText(memberToEdit.getFirstName());
        firstNameField.setPromptText(LanguageManager.getInstance().getString("txt.firstNamePrompt"));

        grid.add(new Label(LanguageManager.getInstance().getString("table.lastName")), 0, 1);
        grid.add(lastNameField, 1, 1);
        lastNameField.setText(memberToEdit.getLastName());

        grid.add(new Label(LanguageManager.getInstance().getString("table.membershiplevel")), 0, 2);
        grid.add(levelBox, 1, 2);

        getDialogPane().setContent(grid);

        // Focus
        Platform.runLater(firstNameField::requestFocus);

        // Enable/disable button
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.disableProperty().bind(
                firstNameField.textProperty().isEmpty()
                        .or(lastNameField.textProperty().isEmpty())
        );

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                memberToEdit.setFirstName(firstNameField.getText().trim());
                memberToEdit.setLastName(lastNameField.getText().trim());
                String selectedString = levelBox.getValue();
                MembershipLevel level = MembershipLevel.valueOf(selectedString.toUpperCase());
                memberToEdit.setMembershipLevel(level);
                return memberToEdit;
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