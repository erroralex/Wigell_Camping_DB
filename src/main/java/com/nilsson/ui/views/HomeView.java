package com.nilsson.ui.views;

import com.nilsson.util.LanguageManager;
import com.nilsson.registries.Inventory;
import com.nilsson.registries.MemberRegistry;
import com.nilsson.registries.RentalRegistry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class HomeView extends VBox {

    public HomeView() {
        // 1. Layout Setup
        this.setPadding(new Insets(40));
        this.setSpacing(30);
        this.setAlignment(Pos.TOP_CENTER);
        this.getStyleClass().add("home-view");

        // 2. Logo Section
        ImageView logoView = loadLogo();

        // 3. Welcome Text Section (Hero)
        VBox welcomeBox = createWelcomeSection();

        // 4. Dashboard Stats Cards
        HBox statsContainer = createStatsRow();

        // Add everything to main view
        this.getChildren().addAll(logoView, welcomeBox, statsContainer);
    }

    private ImageView loadLogo() {
        Image logoImage = null;
        try {
            logoImage = new Image(getClass().getResource("/logo.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load logo.png");
        }

        ImageView imageView = new ImageView();
        if (logoImage != null) {
            imageView.setImage(logoImage);
            imageView.setFitWidth(600); // Slightly smaller for better proportion
            imageView.setPreserveRatio(true);

            // Add a subtle drop shadow to the logo for depth
            imageView.setEffect(new DropShadow(20, Color.color(0,0,0,0.3)));
        }
        return imageView;
    }

    private VBox createWelcomeSection() {
        Label welcomeTitle = new Label(LanguageManager.getInstance().getString("txt.welcome").split("\n")[0]); // Just the "Welcome" part
        welcomeTitle.getStyleClass().add("welcome-title");

        Label subTitle = new Label("System Status & Overview");
        subTitle.getStyleClass().add("welcome-subtitle");

        VBox box = new VBox(10, welcomeTitle, subTitle);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private HBox createStatsRow() {
        // Fetch Live Data
        int memberCount = MemberRegistry.getInstance().getMembers().size();
        int vehicleCount = Inventory.getInstance().getRecreationalVehicleList().size();
        int gearCount = Inventory.getInstance().getGearList().size();

        long activeRentals = RentalRegistry.getInstance().getRentals().stream()
                .filter(r -> r.getStatus() == null || "ACTIVE".equalsIgnoreCase(r.getStatus()))
                .count();

        // --- UPDATED: Passing specific tooltip text for each card ---

        VBox memberCard = createStatCard(
                LanguageManager.getInstance().getString("nav.members"),
                String.valueOf(memberCount),
                FontAwesome.USERS,
                "card-blue",
                "Manage registered members and view details" // <--- Tooltip Text
        );

        VBox vehicleCard = createStatCard(
                LanguageManager.getInstance().getString("nav.vehicles"),
                String.valueOf(vehicleCount),
                FontAwesome.TRUCK,
                "card-green",
                "View fleet status and add new vehicles" // <--- Tooltip Text
        );

        VBox gearCard = createStatCard(
                LanguageManager.getInstance().getString("nav.gear"),
                String.valueOf(gearCount),
                FontAwesome.WRENCH,
                "card-orange",
                "Check inventory for tents, chairs, and other equipment" // <--- Tooltip Text
        );

        VBox rentalCard = createStatCard(
                LanguageManager.getInstance().getString("nav.rentals"),
                String.valueOf(activeRentals),
                FontAwesome.CALENDAR_CHECK_O,
                "card-red",
                "See currently active rentals and returns" // <--- Tooltip Text
        );

        HBox row = new HBox(20, memberCard, vehicleCard, gearCard, rentalCard);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    // --- UPDATED SIGNATURE: Added 'String tooltipText' ---
    private VBox createStatCard(String title, String value, FontAwesome iconCode, String colorClass, String tooltipText) {
        // Icon
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(40);
        icon.getStyleClass().add("card-icon");

        // Value (The Number)
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        // Container
        VBox card = new VBox(10, icon, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setPrefHeight(180);

        // Add specific color class + generic card class
        card.getStyleClass().addAll("dashboard-card", colorClass);

        // Drop Shadow
        card.setEffect(new DropShadow(10, Color.color(0,0,0,0.15)));

        // --- USE THE PASSED TEXT ---
        Tooltip tooltip = new Tooltip(tooltipText);

        // Optional Styling
        tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: #333333; -fx-text-fill: white;");

        // Install
        Tooltip.install(card, tooltip);

        return card;
    }
}