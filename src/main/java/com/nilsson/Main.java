package com.nilsson;

import com.nilsson.repo.*;
import com.nilsson.service.*;
import com.nilsson.ui.CustomTitleBar;
import com.nilsson.ui.ResizeHelper;
import com.nilsson.ui.RootLayout;
import com.nilsson.ui.ServiceContainer;
import com.nilsson.ui.views.LoginView;
import com.nilsson.util.HibernateUtil;
import com.nilsson.util.UserSession;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.SessionFactory;

public class Main extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    private SessionTimerService sessionTimerService;
    private RootLayout rootLayout;
    private CustomTitleBar customTitleBar;
    private Runnable onLogout;
    private Runnable onLanguageChange;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Removes Standard Title Bar
            primaryStage.initStyle(StageStyle.UNDECORATED);

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

            // Repos
            GearRepository gearRepo = new GearRepositoryImpl(sessionFactory);
            TentRepository tentRepo = new TentRepositoryImpl(sessionFactory);
            VehicleRepository vehicleRepo = new VehicleRepositoryImpl(sessionFactory);
            MemberRepository memberRepo = new MemberRepositoryImpl(sessionFactory);
            RentalRepository rentalRepo = new RentalRepositoryImpl(sessionFactory);
            ProfitRepository profitRepo = new ProfitRepositoryImpl(sessionFactory);

            // Services
            AuthService authService = new AuthService();
            InventoryService inventoryService = new InventoryService(gearRepo, tentRepo, vehicleRepo);
            MemberService memberService = new MemberService(memberRepo, rentalRepo);
            RentalService rentalService = new RentalService(rentalRepo, vehicleRepo, tentRepo, gearRepo);
            ProfitsService profitService = new ProfitsService(rentalRepo, profitRepo, memberRepo);

            // Service Container
            ServiceContainer services = new ServiceContainer(
                    authService,
                    memberService,
                    rentalService,
                    inventoryService,
                    profitService);

            // Custom Title Bar
            customTitleBar = new CustomTitleBar(primaryStage, this::handleCloseOrLogout);

            // Instantiate Session Timer and Static UserSession
            sessionTimerService = new SessionTimerService(customTitleBar);
            UserSession.initialize(customTitleBar);

            // On Logout:
            onLogout = () -> {
                UserSession.logout();
                showLoginView(primaryStage, rootLayout, authService);
            };

            // On Language change:
            onLanguageChange = () -> {
                customTitleBar.updateTexts();
                rootLayout = new RootLayout(
                        primaryStage,
                        onLogout,
                        customTitleBar,
                        onLanguageChange,
                        services);

                BorderPane contentWrapper = new BorderPane();
                contentWrapper.setTop(customTitleBar);
                contentWrapper.setCenter(rootLayout);
                primaryStage.getScene().setRoot(contentWrapper);
            };

            // Instantiate the single RootLayout
            rootLayout = new RootLayout(
                    primaryStage,
                    onLogout,
                    customTitleBar,
                    onLanguageChange,
                    services);

            // Show the initial login view
            showLoginView(primaryStage, rootLayout, authService);

            // Initial Scene Setup
            Scene scene = primaryStage.getScene();

            // CSS for styling
            String cssPath = getClass().getResource("/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            // Resize Listeners
            ResizeHelper.addResizeListener(primaryStage);

            // Set the stage properties and show the application.
            primaryStage.setTitle("Wigell Camping - Login");
            primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("/icon.png").toExternalForm()));

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading resources or starting application:");
            e.printStackTrace();
        }
    }

    // Handles the clean shutdown logic when pressing the 'X' button.
    private void handleCloseOrLogout() {
        // Stop the Timer Thread
        if (sessionTimerService != null) {
            sessionTimerService.stop();
        }
        // Log out the user
        UserSession.logout();
        // Close the application
        if (getPrimaryStage() != null) {
            getPrimaryStage().close();
        }
    }

    private void showLoginView(Stage stage, RootLayout rootLayout, AuthService authService) {
        LoginView loginView = new LoginView(stage, rootLayout, authService);

        if (customTitleBar != null) {
            customTitleBar.setTimerVisible(false);
        }

        // Wrapper for Title Bar on Login
        BorderPane loginWrapper = new BorderPane();
        loginWrapper.setTop(customTitleBar);
        loginWrapper.setCenter(loginView);

        Scene scene = stage.getScene();

        if (scene == null) {
            scene = new Scene(loginWrapper, 1200, 800);

            // Load CSS
            String css = getClass().getResource("/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.show();
        } else {
            scene.setRoot(loginWrapper);
        }
    }

    private Stage getPrimaryStage() {
        return (Stage) customTitleBar.getScene().getWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}