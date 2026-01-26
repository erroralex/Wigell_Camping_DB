package com.nilsson.ui;

import com.nilsson.util.LanguageManager;
import com.nilsson.util.UserSession;
import com.nilsson.ui.views.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class SideNavigation extends VBox {

    private final RootLayout rootLayout;
    private final Stage primaryStage;
    private final Runnable onLogout;
    private final Runnable onLanguageChange;

    private Button activeButton;
    private final VBox inventorySubMenu;
    private FontIcon inventoryToggleIcon;
    private final Button btnInventory;
    private FontIcon themeIcon;

    private final ProfitsView profitsView;
    private final RentalView rentalView;
    private final HomeView homeView;
    private final MemberView memberView;
    private final VehicleView vehicleView;
    private final GearView gearView;


    public SideNavigation(RootLayout rootLayout,
                          Stage primaryStage,
                          Runnable onLogout,
                          Runnable onLanguageChange,
                          ServiceContainer services) {

        this.rootLayout = rootLayout;
        this.primaryStage = primaryStage;
        this.onLogout = onLogout;
        this.onLanguageChange = onLanguageChange;
        this.profitsView = new ProfitsView();

        this.homeView = new HomeView(services);

        this.rentalView = new RentalView(
                services,
                this.profitsView::updateView
        );

        this.vehicleView = new VehicleView(services.getInventoryService());
        this.gearView = new GearView(services.getInventoryService());
        this.memberView = new MemberView(services.getMemberService());

        // Apply CSS class for the side navigation container
        this.getStyleClass().add("side-navigation");
        this.setSpacing(10);
        this.setPadding(new Insets(20));

        // Initialize the sub-menu VBox
        this.inventorySubMenu = new VBox(0);
        this.inventorySubMenu.setPadding(new Insets(0, 0, 0, 15)); // Indentation
        this.inventorySubMenu.getStyleClass().add("sub-menu");

        // ──────────────────────────────────────────────────────
        // Top Profile Area
        // ──────────────────────────────────────────────────────

        FontIcon userIcon = new FontIcon(FontAwesome.USER);
        userIcon.setIconSize(40);
        userIcon.getStyleClass().add("profile-icon");

        Label usernameLabel = new Label(UserSession.getCurrentUsername());

        VBox profileArea = new VBox(userIcon, usernameLabel);
        profileArea.getStyleClass().add("profile-area");
        profileArea.setAlignment(Pos.CENTER);

        profileArea.setSpacing(5);
        this.getChildren().add(profileArea);

        // ──────────────────────────────────────────────────────
        // Main Navigation Buttons
        // ──────────────────────────────────────────────────────

        // Home Button
        Button btnHome = createNavButton(LanguageManager.getInstance().getString("nav.home"), FontAwesome.HOME);
        btnHome.setOnAction(e -> {
            rootLayout.setContent(homeView);
            setActiveButton(btnHome);
        });

        // Members Button
        Button btnMembers = createNavButton(LanguageManager.getInstance().getString("nav.members"), FontAwesome.USERS);
        btnMembers.setOnAction(e -> {
            memberView.refreshData();
            rootLayout.setContent(memberView);
            setActiveButton(btnMembers);
        });

        // Rentals Button
        Button btnRentals = createNavButton(LanguageManager.getInstance().getString("nav.rentals"), FontAwesome.PRODUCT_HUNT);
        btnRentals.setOnAction(e -> {
            rentalView.refreshData();
            rootLayout.setContent(rentalView);
            setActiveButton(btnRentals);
        });

        // Profits Button
        Button btnProfits = createNavButton(LanguageManager.getInstance().getString("nav.profits"), FontAwesome.MONEY);
        btnProfits.setOnAction(e -> {
            rootLayout.setContent(profitsView);
            // Valid now that updateView() exists
            profitsView.updateView();
            setActiveButton(btnProfits);
        });

        // ──────────────────────────────────────────────────────
        // Inventory Toggle Button (Collapsible)
        // ──────────────────────────────────────────────────────

        // Initialize and store the arrow icon
        FontIcon angleIcon = new FontIcon(FontAwesome.ANGLE_RIGHT);
        this.inventoryToggleIcon = angleIcon;

        // Create the button
        this.btnInventory = createToggleNavButton(LanguageManager.getInstance().getString("nav.inventory"), FontAwesome.CUBES, angleIcon);

        // Toggle Action
        btnInventory.setOnAction(e -> {
            // Toggle visibility of the sub-menu
            boolean isVisible = inventorySubMenu.isVisible();
            inventorySubMenu.setVisible(!isVisible);
            inventorySubMenu.setManaged(!isVisible);

            // Toggle the arrow icon appearance
            if (inventorySubMenu.isVisible()) {
                this.inventoryToggleIcon.setIconLiteral(FontAwesome.ANGLE_DOWN.getDescription());
                btnInventory.getStyleClass().add("nav-button-toggle-active");
            } else {
                this.inventoryToggleIcon.setIconLiteral(FontAwesome.ANGLE_RIGHT.getDescription());
                btnInventory.getStyleClass().remove("nav-button-toggle-active");
            }
        });

        // ──────────────────────────────────────────────────────
        // Sub-Buttons (Vehicles and Gear)
        // ──────────────────────────────────────────────────────

        // Vehicle Sub-Button
        Button btnVehicle = createSubNavButton(LanguageManager.getInstance().getString("nav.vehicles"), FontAwesome.CAR);
        btnVehicle.setOnAction(e -> {
            vehicleView.refreshData();
            rootLayout.setContent(vehicleView);
            setActiveButton(btnVehicle);
        });

        // Gear Sub-Button
        Button btnGear = createSubNavButton(LanguageManager.getInstance().getString("nav.gear"), FontAwesome.FREE_CODE_CAMP);
        btnGear.setOnAction(e -> {
            gearView.refreshData();
            rootLayout.setContent(gearView);
            setActiveButton(btnGear);
        });

        // Add sub-buttons to the sub-menu container
        inventorySubMenu.getChildren().addAll(btnVehicle, btnGear);

        // Initially hide the sub-menu
        inventorySubMenu.setVisible(false);
        inventorySubMenu.setManaged(false);

        homeView.setupNavActions(
                btnMembers::fire, // On Member Card Click
                () -> {           // On Vehicle Card Click
                    ensureInventoryExpanded();
                    btnVehicle.fire();
                },
                () -> {           // On Gear Card Click
                    ensureInventoryExpanded();
                    btnGear.fire();
                },
                btnRentals::fire  // On Rental Card Click
        );

        // ──────────────────────────────────────────────────────
        // Spacer for Bottom Alignment
        // ──────────────────────────────────────────────────────

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ──────────────────────────────────────────────────────
        // Theme Toggle Button
        // ──────────────────────────────────────────────────────

        // Start in dark mode
        this.themeIcon = new FontIcon(FontAwesome.SUN_O);
        Button btnThemeToggle = createNavButton(LanguageManager.getInstance().getString("nav.toggleLight"), this.themeIcon);

        btnThemeToggle.setOnAction(e -> {
            boolean isNowDark = rootLayout.toggleTheme();
            if (isNowDark) {
                // Dark mode
                this.themeIcon.setIconLiteral(FontAwesome.SUN_O.getDescription());
                btnThemeToggle.setText(LanguageManager.getInstance().getString("nav.toggleLight"));
            } else {
                // Light mode
                this.themeIcon.setIconLiteral(FontAwesome.MOON_O.getDescription());
                btnThemeToggle.setText(LanguageManager.getInstance().getString("nav.toggleDark"));
            }
        });

        // ──────────────────────────────────────────────────────
        // Language Toggle Button
        // ──────────────────────────────────────────────────────

        FontIcon languageIcon = new FontIcon(FontAwesome.GLOBE);
        Button btnLanguage = createNavButton(LanguageManager.getInstance().getString("nav.toggleLanguage"), languageIcon);
        btnLanguage.setOnAction(e -> {
            LanguageManager.getInstance().toggleLanguage();
            onLanguageChange.run();
        });

        // Logout Button
        Button btnLogout = createNavButton(LanguageManager.getInstance().getString("nav.logout"), FontAwesome.SIGN_OUT);
        btnLogout.getStyleClass().add("btnExit");
        btnLogout.setOnAction(e -> {
            UserSession.logout();
            onLogout.run();
        });

        // Add to VBox
        this.getChildren().addAll(btnHome, btnMembers, btnInventory, inventorySubMenu, btnRentals, btnProfits, spacer, btnThemeToggle, btnLanguage, btnLogout);

        // Set initial active button
        setActiveButton(btnHome);
    }

    public HomeView getHomeView() {
        return this.homeView;
    }

    // ──────────────────────────────────────────────────────
    // Helper Methods
    // ──────────────────────────────────────────────────────

    // Helper to create a standard navigation button
    private Button createNavButton(String text, Ikon iconCode) {
        Button btn = new Button(text);
        FontIcon icon = new FontIcon(iconCode);

        icon.setIconSize(20);
        icon.getStyleClass().add("nav-icon");

        btn.setGraphic(icon);

        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphicTextGap(10);
        return btn;
    }

    // Overloaded helper to create a standard navigation button using an existing FontIcon
    private Button createNavButton(String text, FontIcon icon) {
        Button btn = new Button(text);

        icon.setIconSize(20);
        icon.getStyleClass().add("nav-icon");

        btn.setGraphic(icon);

        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphicTextGap(10);
        return btn;
    }

    // Helper to create the toggle button (Inventory). It combines the main icon with an arrow toggle indicator.
    private Button createToggleNavButton(String text, Ikon mainIconCode, FontIcon toggleIcon) {
        Button btn = new Button(text);

        // Main Icon
        FontIcon mainIcon = new FontIcon(mainIconCode);
        mainIcon.setIconSize(20);
        mainIcon.getStyleClass().add("nav-icon");

        // Toggle Icon
        toggleIcon.setIconSize(14);
        toggleIcon.getStyleClass().add("nav-icon-toggle");

        btn.setGraphic(UIUtil.createIconWithSpacer(mainIcon, toggleIcon));

        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphicTextGap(10);

        btn.getStyleClass().add("nav-button-toggle");

        return btn;
    }

    // Helper to create a sub-button (Vehicles/Gear)
    private Button createSubNavButton(String text, Ikon iconCode) {
        Button btn = createNavButton(text, iconCode);
        btn.getStyleClass().add("sub-nav-button");
        return btn;
    }

    // Sets the active state styling for the clicked button
    private void setActiveButton(Button newActiveButton) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        newActiveButton.getStyleClass().add("nav-button-active");
        activeButton = newActiveButton;

        // If a sub-button is clicked, ensure the parent toggle button shows a visual cue
        if (newActiveButton.getStyleClass().contains("sub-nav-button")) {
            // Check if the parent button is not already marked as active toggle
            if (!btnInventory.getStyleClass().contains("nav-button-toggle-active")) {
                btnInventory.getStyleClass().add("nav-button-toggle-active");
                // Ensure the arrow is down if one of its children is active
                this.inventoryToggleIcon.setIconLiteral(FontAwesome.ANGLE_DOWN.getDescription());
            }
        } else {
            // If a main button (Home, Members) is clicked, ensure the Inventory button is NOT marked as active toggle
            if (activeButton != btnInventory) {
                btnInventory.getStyleClass().remove("nav-button-toggle-active");
                // Reset the arrow if it's not the active button
                if (this.inventoryToggleIcon.getIconLiteral().equals(FontAwesome.ANGLE_DOWN.getDescription())) {
                    this.inventoryToggleIcon.setIconLiteral(FontAwesome.ANGLE_RIGHT.getDescription());
                }
            }
        }
    }

    // Helper to auto-expand submenu when clicking Home cards
    private void ensureInventoryExpanded() {
        if (!inventorySubMenu.isVisible()) {
            inventorySubMenu.setVisible(true);
            inventorySubMenu.setManaged(true);
            this.inventoryToggleIcon.setIconLiteral(FontAwesome.ANGLE_DOWN.getDescription());
            btnInventory.getStyleClass().add("nav-button-toggle-active");
        }
    }
}