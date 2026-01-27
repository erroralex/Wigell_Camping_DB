package com.nilsson.ui.views;

import com.nilsson.service.InventoryService;
import com.nilsson.service.MemberService;
import com.nilsson.service.RentalService;
import com.nilsson.ui.ServiceContainer;
import com.nilsson.ui.UIUtil;
import com.nilsson.util.LanguageManager;
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
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class HomeView extends VBox {

    private final MemberService memberService;
    private final InventoryService inventoryService;
    private final RentalService rentalService;

    private VBox memberCard;
    private VBox vehicleCard;
    private VBox gearCard;
    private VBox rentalCard;

    public HomeView(ServiceContainer services) {
        this.memberService = services.getMemberService();
        this.inventoryService = services.getInventoryService();
        this.rentalService = services.getRentalService();

        // Layout Setup
        this.setPadding(new Insets(40));
        this.setSpacing(30);
        this.setAlignment(Pos.TOP_CENTER);
        this.getStyleClass().add("home-view");

        // Logo Section
        ImageView logoView = loadLogo();

        // Welcome Text Section
        VBox welcomeBox = createWelcomeSection();

        // Dashboard Stats Cards
        HBox statsContainer = createStatsRow();

        // Add everything to the view
        this.getChildren().addAll(logoView, welcomeBox, statsContainer);
    }

    public void setupNavActions(Runnable onMember, Runnable onVehicle, Runnable onGear, Runnable onRental) {
        if (memberCard != null) memberCard.setOnMouseClicked(e -> onMember.run());
        if (vehicleCard != null) vehicleCard.setOnMouseClicked(e -> onVehicle.run());
        if (gearCard != null) gearCard.setOnMouseClicked(e -> onGear.run());
        if (rentalCard != null) rentalCard.setOnMouseClicked(e -> onRental.run());
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
            imageView.setFitWidth(700);
            imageView.setPreserveRatio(true);

            // Add a subtle drop shadow to the logo for depth
            imageView.setEffect(new DropShadow(20, Color.color(0,0,0,0.3)));
        }
        return imageView;
    }

    private VBox createWelcomeSection() {
        Label welcomeTitle = new Label(LanguageManager.getInstance().getString("txt.welcome").split("\n")[0]); // Just the "Welcome" part
        welcomeTitle.getStyleClass().add("welcome-title");

        Label subTitle = new Label(LanguageManager.getInstance().getString("txt.welcomeTitle"));
        subTitle.getStyleClass().add("welcome-subtitle");

        VBox box = new VBox(10, welcomeTitle, subTitle);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private HBox createStatsRow() {
        int memberCount = 0;
        int vehicleCount = 0;
        int gearCount = 0;
        long activeRentals = 0;

        try {
            // Fetch data
            memberCount = memberService.getAllMembers().size();
            vehicleCount = inventoryService.getAllVehicles().size();
            gearCount = inventoryService.getAllGear().size() + inventoryService.getAllTents().size();
            activeRentals = rentalService.getAllRentals().stream().filter(
                    rental -> rental.getEndTime() == null).count();
        } catch (Exception e) {
            UIUtil.showErrorAlert(
                    LanguageManager.getInstance().getString("error.dashboardError"),
                    LanguageManager.getInstance().getString("error.statisticsError"),
                    LanguageManager.getInstance().getString("error.databaseConnection")
            );
        }

        this.memberCard = createStatCard(
                LanguageManager.getInstance().getString("memberCard"),
                String.valueOf(memberCount),
                FontAwesome.USERS,
                "card-blue",
                LanguageManager.getInstance().getString("tooltip.memberCard")
        );

        this.vehicleCard = createStatCard(
                LanguageManager.getInstance().getString("vehicleCard"),
                String.valueOf(vehicleCount),
                FontAwesome.TRUCK,
                "card-green",
                LanguageManager.getInstance().getString("tooltip.vehicleCard")
        );

        this.gearCard = createStatCard(
                LanguageManager.getInstance().getString("gearCard"),
                String.valueOf(gearCount),
                FontAwesome.WRENCH,
                "card-orange",
                LanguageManager.getInstance().getString("tooltip.gearCard")
        );

        this.rentalCard = createStatCard(
                LanguageManager.getInstance().getString("rentalCard"),
                String.valueOf(activeRentals),
                FontAwesome.CALENDAR_CHECK_O,
                "card-red",
                LanguageManager.getInstance().getString("tooltip.rentalCard")
        );

        HBox row = new HBox(20, memberCard, vehicleCard, gearCard, rentalCard);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox createStatCard(String title, String value, FontAwesome iconCode, String colorClass, String tooltipText) {
        // Icon
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(40);
        icon.getStyleClass().add("card-icon");

        // Value
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        // Wrapping
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // Container
        VBox card = new VBox(10, icon, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setMinHeight(180);

        // Add specific color class + generic card class
        card.getStyleClass().addAll("dashboard-card", colorClass);

        // Drop Shadow
        card.setEffect(new DropShadow(10, Color.color(0,0,0,0.15)));

        // Use the text
        Tooltip tooltip = new Tooltip(tooltipText);

        // Optional Styling
        tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: #333333; -fx-text-fill: white;");

        // Install
        Tooltip.install(card, tooltip);

        return card;
    }
}