package com.nilsson.ui.dialogs;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Member;
import com.nilsson.ui.UIUtil;
import javafx.collections.FXCollections;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class HistoryDialog extends Dialog<Void> {

    public HistoryDialog(Member member) {

        // Dialog setup
        setTitle(LanguageManager.getInstance().getString("txt.historyTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.historyHeader") + " " + member.getFirstName() +
                " " + member.getLastName() + " (ID: " + member.getId() + ")");

        this.setOnShowing(dialogEvent -> {
            UIUtil.applyDialogSetup(this);
            getDialogPane().getStyleClass().add("history-dialog");
        });

        getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        // ListView for presentation
        VBox content = new VBox(10);
        content.setPrefSize(600, 300);

        List<String> historyList = member.getHistory();

        if (historyList == null || historyList.isEmpty()) {
            // No history.
            content.getChildren().add(new javafx.scene.control.Label(member.getFirstName() +
                    LanguageManager.getInstance().getString("txt.noRecorded")));
        } else {
            // History exists
            ListView<String> historyListView = new ListView<>(FXCollections.observableArrayList(historyList));

            // Add custom style class
            historyListView.getStyleClass().add("history-list-view");

            // Allow the list view to grow to fill the VBox
            VBox.setVgrow(historyListView, javafx.scene.layout.Priority.ALWAYS);

            content.getChildren().add(historyListView);
        }

        getDialogPane().setContent(content);
        setResizable(true);
    }
}