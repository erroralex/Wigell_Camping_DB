package com.nilsson.ui.dialogs;

import com.nilsson.entity.*;
import com.nilsson.model.NewRentalResult;
import com.nilsson.repo.MemberRepositoryImpl;
import com.nilsson.service.InventoryService;
import com.nilsson.service.MemberService;
import com.nilsson.ui.UIUtil;
import com.nilsson.util.HibernateUtil;
import com.nilsson.util.LanguageManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class NewRentalDialog extends Dialog<NewRentalResult> {

    private final MemberService memberService;
    private final InventoryService inventoryService;

    private final ComboBox<Member> memberBox = new ComboBox<>();
    private final ComboBox<RentalType> itemTypeBox = new ComboBox<>(); // Changed to Enum

    private final ComboBox<Gear> gearBox = new ComboBox<>();
    private final ComboBox<Vehicle> vehicleBox = new ComboBox<>();
    private final ComboBox<Tent> tentBox = new ComboBox<>();

    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());

    public NewRentalDialog(MemberService memberService, InventoryService inventoryService) {
        this.memberService = memberService;
        this.inventoryService = inventoryService;

        setTitle(LanguageManager.getInstance().getString("txt.newRentalTitle"));
        setHeaderText(LanguageManager.getInstance().getString("txt.newRentalHeader"));

        // Setup Buttons
        ButtonType createBtnType = new ButtonType(LanguageManager.getInstance().getString("btn.createRental"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 1. Member Selection
        memberBox.setPromptText(LanguageManager.getInstance().getString("txt.selectMember"));
        memberBox.getItems().addAll(memberService.getAllMembers());
        // Custom Cell Factory to show Name + ID
        memberBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getFirstName() + " " + item.getLastName() + " (ID: " + item.getId() + ")");
            }
        });
        memberBox.setButtonCell(memberBox.getCellFactory().call(null)); // Apply to selected view

        // 2. Type Selection (Use the Enum directly)
        itemTypeBox.getItems().setAll(RentalType.values());
        itemTypeBox.setValue(RentalType.GEAR); // Default

        // 3. Load Available Items
        loadAvailableItems();

        // 4. Layout
        grid.add(new Label(LanguageManager.getInstance().getString("table.member")), 0, 0);
        grid.add(memberBox, 1, 0);

        grid.add(new Label(LanguageManager.getInstance().getString("txt.itemType")), 0, 1);
        grid.add(itemTypeBox, 1, 1);

        // Stack the specific item boxes in the same grid cell
        grid.add(new Label("Item:"), 0, 2);
        grid.add(gearBox, 1, 2);
        grid.add(vehicleBox, 1, 2);
        grid.add(tentBox, 1, 2);

        grid.add(new Label(LanguageManager.getInstance().getString("table.startDate")), 0, 3);
        grid.add(startDatePicker, 1, 3);

        getDialogPane().setContent(grid);

        // 5. Logic
        configureItemBoxes(); // Helper to set cell factories
        updateVisibility();   // Initial visibility check

        itemTypeBox.valueProperty().addListener((obs, oldV, newV) -> updateVisibility());

        // Enable/Disable Create Button
        Button createBtn = (Button) getDialogPane().lookupButton(createBtnType);
        createBtn.disableProperty().bind(
                memberBox.valueProperty().isNull()
                        .or(startDatePicker.valueProperty().isNull())
        );

        Platform.runLater(memberBox::requestFocus);

        // 6. Convert Result
        setResultConverter(dialogButton -> {
            if (dialogButton == createBtnType) {
                return buildResult();
            }
            return null;
        });
    }

    private void configureItemBoxes() {
        // Formatter for Gear
        gearBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Gear item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getModel() + " (" + item.getType() + ")");
            }
        });
        gearBox.setButtonCell(gearBox.getCellFactory().call(null));

        // Formatter for Vehicle
        vehicleBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getMake() + " " + item.getModel() + " - " + item.getId());
            }
        });
        vehicleBox.setButtonCell(vehicleBox.getCellFactory().call(null));

        // Formatter for Tent
        tentBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Tent item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getModel() + " (Cap: " + item.getCapacity() + ")");
            }
        });
        tentBox.setButtonCell(tentBox.getCellFactory().call(null));
    }

    private void updateVisibility() {
        RentalType type = itemTypeBox.getValue();

        gearBox.setVisible(type == RentalType.GEAR);
        gearBox.setManaged(type == RentalType.GEAR);

        vehicleBox.setVisible(type == RentalType.VEHICLE);
        vehicleBox.setManaged(type == RentalType.VEHICLE);

        tentBox.setVisible(type == RentalType.TENT);
        tentBox.setManaged(type == RentalType.TENT);
    }

    private void loadAvailableItems() {
        gearBox.getItems().setAll(inventoryService.getAllGear().stream().filter(i -> !i.isRented()).collect(Collectors.toList()));
        vehicleBox.getItems().setAll(inventoryService.getAllVehicles().stream().filter(i -> !i.isRented()).collect(Collectors.toList()));
        tentBox.getItems().setAll(inventoryService.getAllTents().stream().filter(i -> !i.isRented()).collect(Collectors.toList()));
    }

    private NewRentalResult buildResult() {
        Member member = memberBox.getValue();
        RentalType type = itemTypeBox.getValue();
        Object selectedItem = null;

        if (type == RentalType.GEAR) selectedItem = gearBox.getValue();
        else if (type == RentalType.VEHICLE) selectedItem = vehicleBox.getValue();
        else if (type == RentalType.TENT) selectedItem = tentBox.getValue();

        if (selectedItem == null) {
            UIUtil.showErrorAlert("Validation Error", "No Item Selected", "Please select an item to rent.");
            return null;
        }

        // Logic: Combine the selected Date with Current Time
        // If date is today -> Use LocalTime.now()
        // If date is past -> Use Start of day (00:00) or Noon? Let's use Noon to be safe, or 12:00.
        // Best practice for "Check Out" is usually NOW.

        LocalDate selectedDate = startDatePicker.getValue();
        LocalTime timePart = LocalTime.now();

        // If the admin backdates to yesterday, we probably want 08:00 or something, but keeping current time is usually fine for calculation
        LocalDateTime startDateTime = LocalDateTime.of(selectedDate, timePart);

        return new NewRentalResult(member, selectedItem, type, startDateTime);
    }
}