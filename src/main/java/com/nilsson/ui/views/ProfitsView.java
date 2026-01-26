package com.nilsson.ui.views;

import com.nilsson.entity.DailyProfit;
import com.nilsson.repo.*;
import com.nilsson.service.ProfitsService;
import com.nilsson.util.HibernateUtil;
import com.nilsson.util.LanguageManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProfitsView extends VBox {

    private final DailyProfitRepository profitRepo;
    private final RentalRepository rentalRepo;
    private final MemberRepository memberRepo;
    private final ProfitsService profitsService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");

    private final Label incomeTodayValueLabel = new Label();
    private final Label totalLabelValue = new Label();
    private final XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();

    public ProfitsView() {
        this.profitRepo = new DailyProfitRepositoryImpl();
        this.rentalRepo = new RentalRepositoryImpl(HibernateUtil.getSessionFactory());
        this.memberRepo = new MemberRepositoryImpl(HibernateUtil.getSessionFactory());
        this.profitsService = new ProfitsService(rentalRepo, profitRepo, memberRepo);

        this.getStyleClass().add("content-view");
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);

        Label title = new Label(LanguageManager.getInstance().getString("txt.rentalIncome"));
        title.getStyleClass().add("content-title");

        HBox incomeTodayBox = createIncomeTodayBox();
        HBox totalIncomeBox = createTotalIncomeBox();

        BarChart<String, Number> incomeChart = createIncomeBarChart();
        incomeChart.getData().add(profitSeries);
        VBox.setVgrow(incomeChart, Priority.ALWAYS);

        Button btnRefresh = new Button();
        btnRefresh.setGraphic(new FontIcon(FontAwesome.REFRESH));
        btnRefresh.setOnAction(e -> updateView());
        btnRefresh.getStyleClass().add("action-button");

        this.getChildren().addAll(title, incomeTodayBox, totalIncomeBox, btnRefresh, incomeChart);

        updateView();
    }

    public void updateView() {
        try {
            profitsService.recalculateProfits();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigDecimal incomeToday = profitsService.getIncomeToday();
        BigDecimal totalIncome = profitsService.calculateTotalIncome();
        List<DailyProfit> allProfits = profitsService.getAllDailyProfits();

        incomeTodayValueLabel.setText(String.format("%,.2f SEK", incomeToday));
        totalLabelValue.setText(String.format("%,.2f SEK", totalIncome));

        updateChartData(allProfits);
    }

    private void updateChartData(List<DailyProfit> allProfits) {
        LocalDate today = LocalDate.now();
        LocalDate fourteenDaysAgo = today.minusDays(14);

        List<DailyProfit> recentProfits = allProfits.stream()
                .filter(p -> !p.getDate().isBefore(fourteenDaysAgo))
                .filter(p -> !p.getDate().isAfter(today))
                .sorted(Comparator.comparing(DailyProfit::getDate))
                .collect(Collectors.toList());

        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();
        for (DailyProfit profit : recentProfits) {
            chartData.add(new XYChart.Data<>(
                    profit.getDate().format(DATE_FORMATTER),
                    profit.getIncome().doubleValue()
            ));
        }

        profitSeries.getData().clear();
        profitSeries.getData().setAll(chartData);

        Platform.runLater(() -> {
            if (profitSeries.getChart() != null) {
                CategoryAxis xAxis = (CategoryAxis) profitSeries.getChart().getXAxis();
                List<String> categories = chartData.stream()
                        .map(XYChart.Data::getXValue)
                        .distinct()
                        .collect(Collectors.toList());
                xAxis.getCategories().setAll(categories);
            }
        });

        if (!chartData.isEmpty() && profitSeries.getChart() != null) {
            double maxVal = chartData.stream()
                    .mapToDouble(d -> d.getYValue().doubleValue())
                    .max().orElse(0);

            NumberAxis yAxis = (NumberAxis) profitSeries.getChart().getYAxis();
            double upperBound = Math.ceil((maxVal * 1.2) / 100) * 100;

            if (upperBound > 0) {
                yAxis.setAutoRanging(false);
                yAxis.setUpperBound(upperBound);
                yAxis.setTickUnit(upperBound / 5);
            } else {
                yAxis.setAutoRanging(true);
            }
        }
    }

    private HBox createIncomeTodayBox() {
        Label incomeTodayLabel = new Label(LanguageManager.getInstance().getString("txt.incomeToday"));
        incomeTodayLabel.getStyleClass().add("income-stat-label");
        incomeTodayValueLabel.getStyleClass().add("income-stat-value");

        HBox box = new HBox(10, incomeTodayLabel, incomeTodayValueLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("income-stats-box");
        return box;
    }

    private HBox createTotalIncomeBox() {
        Label totalLabelDesc = new Label(LanguageManager.getInstance().getString("txt.totalRecIncome"));
        totalLabelDesc.getStyleClass().add("income-stat-label");
        totalLabelValue.getStyleClass().add("income-stat-value");

        HBox box = new HBox(10, totalLabelDesc, totalLabelValue);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("income-stats-box");
        return box;
    }

    private BarChart<String, Number> createIncomeBarChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        yAxis.setAutoRanging(true);
        yAxis.setLabel(LanguageManager.getInstance().getString("y.income"));

        xAxis.setLabel(LanguageManager.getInstance().getString("x.date"));
        xAxis.setAnimated(false);

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(LanguageManager.getInstance().getString("txt.dailyIncome"));
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.getStyleClass().add("profit-chart");

        return barChart;
    }
}