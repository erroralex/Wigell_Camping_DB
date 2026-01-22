package com.nilsson.ui.views;

import com.nilsson.util.LanguageManager;
import com.nilsson.entity.Member;
import com.nilsson.registries.MemberRegistry;
import com.nilsson.service.MembershipService;
import com.nilsson.ui.UIUtil;
import com.nilsson.ui.dialogs.AddMemberDialog;
import com.nilsson.ui.dialogs.HistoryDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Optional;

public class MemberView extends VBox {

    private final TableView<Member> memberTable = new TableView<>();
    // ObservableList som kopplas till tabellen
    private final ObservableList<Member> masterMemberData = FXCollections.observableArrayList();
    private final MembershipService membershipService = new MembershipService();
    private final TextField searchField = new TextField();
    private FilteredList<Member> filteredData;

    public MemberView() {

        // Apply CSS and Layout
        this.getStyleClass().add("content-view");
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(memberTable, Priority.ALWAYS);

        Label title = new Label(LanguageManager.getInstance().getString("txt.memberManagement"));
        title.getStyleClass().add("content-title");

        // Search Field Setup
        searchField.setPromptText(LanguageManager.getInstance().getString("txt.searchMembers"));
        searchField.setMaxWidth(385);

        // Ladda data från databasen via registret
        loadMasterData();

        // Initiera tabellen
        initializeTable();

        // Skapa knappraden
        HBox buttonBar = createButtonBar();

        // Lägg till allt i vyn
        this.getChildren().addAll(title, buttonBar, searchField, memberTable);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable() {

        // ID Column
        TableColumn<Member, Integer> idCol = new TableColumn<>(LanguageManager.getInstance().getString("table.id"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        // First Name Column
        TableColumn<Member, String> firstNameCol = new TableColumn<>(LanguageManager.getInstance().getString("table.firstName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(200);

        // Last Name Column
        TableColumn<Member, String> lastNameCol = new TableColumn<>(LanguageManager.getInstance().getString("table.lastName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(200);

        // Membership Level Column
        TableColumn<Member, String> membershipCol = new TableColumn<>(LanguageManager.getInstance().getString("table.membershiplevel"));
        membershipCol.setCellValueFactory(new PropertyValueFactory<>("membershipLevel"));
        membershipCol.setPrefWidth(150);

        // Add columns to table
        memberTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, membershipCol);

        // Koppla tabellen till vår ObservableList
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Filtering Logic ---
        filteredData = new FilteredList<>(masterMemberData, p -> true);

        // Lyssna på sökfältet
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                // Om sökfältet är tomt, visa alla
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Kolla om något fält matchar
                if (member.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getMembershipLevel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(member.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Ingen matchning
            });
        });

        // Wrappa FilteredList i en SortedList så sortering fungerar med filtret
        SortedList<Member> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(memberTable.comparatorProperty());

        // Sätt datan i tabellen
        memberTable.setItems(sortedData);
    }

    // Hämtar medlemmar från MemberRegistry (som hämtar från DB)
    private void loadMasterData() {
        // Se till att registret har den senaste datan från DB
        // (Valfritt: MemberRegistry.getInstance().refreshRegistry(); om du vill tvinga en DB-fråga här)

        List<Member> members = MemberRegistry.getInstance().getMembers();

        // Använd setAll för att ersätta innehållet istället för att lägga till (undviker dubbletter vid refresh)
        masterMemberData.setAll(members);
    }

    private void handleEditMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.noItemSelected"),
                    LanguageManager.getInstance().getString("error.selectionRequired"),
                    LanguageManager.getInstance().getString("error.pleaseSelectEditItem"));
            return;
        }

        // Service hanterar logiken och DAO-uppdateringen
        membershipService.handleEditMember(selectedMember);

        // Uppdatera tabellen grafiskt (ifall sorteringen ändras etc)
        memberTable.refresh();
    }

    private void handleRemoveMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.missingMember"),
                    LanguageManager.getInstance().getString("error.selectionRequired"),
                    LanguageManager.getInstance().getString("error.pleaseSelectRemoveMember"));
            return;
        }

        boolean confirmed = UIUtil.showConfirmationAlert(
                LanguageManager.getInstance().getString("confirm.removal"),
                LanguageManager.getInstance().getString("confirm.confirm"),
                LanguageManager.getInstance().getString("confirm.selected") + " " +
                        selectedMember.getFirstName() + "?");

        if (confirmed) {
            // Tar bort från DB och registret
            boolean wasRemovedFromRegistry = membershipService.removeMemberFromRegistry(selectedMember);

            if (wasRemovedFromRegistry) {
                // Ta bort från UI-listan också
                masterMemberData.remove(selectedMember);
            } else {
                UIUtil.showErrorAlert(
                        LanguageManager.getInstance().getString("confirm.removal"),
                        LanguageManager.getInstance().getString("error.operationError"),
                        LanguageManager.getInstance().getString("error.messageMember"));
            }
        }
    }

    private void handleShowHistory() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.missingMember"),
                    LanguageManager.getInstance().getString("error.selectionRequired"),
                    LanguageManager.getInstance().getString("error.pleaseSelectMemberHistory"));
            return;
        }

        HistoryDialog historyDialog = new HistoryDialog(selectedMember);
        historyDialog.showAndWait();
    }

    // Metod för att manuellt ladda om data från DB
    public void refreshData() {
        MemberRegistry.getInstance().refreshRegistry(); // Tvinga DAO att hämta från DB
        loadMasterData(); // Uppdatera UI-listan
    }

    private HBox createButtonBar() {
        Button btnAdd = new Button(LanguageManager.getInstance().getString("btn.addMember"));
        btnAdd.getStyleClass().add("action-button");
        btnAdd.setOnAction(actionEvent -> {
            // 1. View handles the Dialog
            AddMemberDialog dialog = new AddMemberDialog();
            Optional<Member> result = dialog.showAndWait();

            // 2. View passes data to Service
            if (result.isPresent()) {
                Member newMember = result.get();
                membershipService.saveNewMember(newMember); // Renamed method
                masterMemberData.add(newMember); // Update UI Table
            }
        });

        Button btnEdit = new Button(LanguageManager.getInstance().getString("btn.editMember"));
        btnEdit.getStyleClass().add("action-button");
        btnEdit.setOnAction(actionEvent -> handleEditMember());

        Button btnRemove = new Button(LanguageManager.getInstance().getString("btn.removeMember"));
        btnRemove.getStyleClass().add("action-button");
        btnRemove.setOnAction(actionEvent -> handleRemoveMember());

        Button btnHistory = new Button(LanguageManager.getInstance().getString("btn.history"));
        btnHistory.getStyleClass().add("action-button");
        btnHistory.setOnAction(actionEvent -> handleShowHistory());

        // --- NY KNAPP: Refresh ---
        // Bra att ha när man jobbar mot en databas
        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.getStyleClass().add("action-button");
        btnRefresh.setOnAction(e -> refreshData());

        // Add to container
        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnRemove, btnHistory, btnRefresh);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        return buttonBar;
    }
}