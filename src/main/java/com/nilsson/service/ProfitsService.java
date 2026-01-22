package com.nilsson.service;

import com.nilsson.entity.DailyProfit;
import com.nilsson.entity.Member;
import com.nilsson.entity.Rental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ProfitsService {

    private final RentalRegistry rentalRegistry = RentalRegistry.getInstance();
    private final Inventory inventory = Inventory.getInstance();
    private final MemberRegistry memberRegistry = MemberRegistry.getInstance();
    private final ProfitDAO profitDAO;
    private final ObservableList<DailyProfit> dailyProfits;

    public ProfitsService() {
        this.profitDAO = new ProfitDAO();
        this.dailyProfits = FXCollections.observableArrayList(profitDAO.getAllProfits());
    }

    public ObservableList<DailyProfit> getObservableDailyProfits() {
        return dailyProfits;
    }

    // --- RESTORED METHOD ---
    public List<DailyProfit> getDailyProfits() {
        return dailyProfits;
    }
    // -----------------------

    public void recalculateProfitsFromRentals() {
        // 1. Calculate Expected Income per Date based on ALL Rentals
        Map<LocalDate, Double> calculatedIncomeMap = new HashMap<>();

        for (Rental rental : rentalRegistry.getRentals()) {
            if (rental.getRentalDays() == null || rental.getRentalDays() <= 0) continue;

            IRentable item = Inventory.getInstance().findItemByIdAndType(rental.getItemId(), rental.getItemType());
            if (item == null) continue;

            // Calculate cost for this specific rental
            double totalCost = calculateRentalRevenue(rental);
            double dailyCost = totalCost / rental.getRentalDays();

            // Distribute cost over the days
            LocalDate start = rental.getStartDate();
            for (int i = 0; i < rental.getRentalDays(); i++) {
                LocalDate day = start.plusDays(i);
                calculatedIncomeMap.put(day, calculatedIncomeMap.getOrDefault(day, 0.0) + dailyCost);
            }
        }

        // 2. Fetch Existing Profits from DB to avoid Duplicates
        List<DailyProfit> existingProfits = profitDAO.getAllProfits();
        Map<LocalDate, DailyProfit> dbProfitMap = existingProfits.stream()
                .collect(Collectors.toMap(DailyProfit::getDate, p -> p, (p1, p2) -> p1));

        // 3. Merge: Update existing records or Create new ones
        List<DailyProfit> recordsToSave = new ArrayList<>();

        // Add/Update calculated days
        for (Map.Entry<LocalDate, Double> entry : calculatedIncomeMap.entrySet()) {
            LocalDate date = entry.getKey();
            Double amount = entry.getValue();

            if (dbProfitMap.containsKey(date)) {
                // UPDATE existing row (keeps the ID)
                DailyProfit existing = dbProfitMap.get(date);
                existing.setIncome(amount);
                recordsToSave.add(existing);
            } else {
                // CREATE new row
                DailyProfit newProfit = new DailyProfit(date, amount);
                recordsToSave.add(newProfit);
            }
        }

        // 4. Save to Database
        if (!recordsToSave.isEmpty()) {
            profitDAO.saveAllProfits(recordsToSave);
        }

        // 5. Update UI List
        dailyProfits.setAll(recordsToSave);
        FXCollections.sort(dailyProfits, Comparator.comparing(DailyProfit::getDate));
    }

    public double calculateTotalIncome() {
        return rentalRegistry.getRentals().stream()
                .mapToDouble(this::calculateRentalRevenue)
                .sum();
    }

    public double getIncomeToday() {
        return dailyProfits.stream()
                .filter(p -> p.getDate().isEqual(LocalDate.now()))
                .mapToDouble(DailyProfit::getIncome)
                .findFirst()
                .orElse(0.0);
    }

    public double calculateRentalRevenue(Rental rental) {
        if (rental.getRentalDays() == null || rental.getRentalDays() <= 0) return 0.0;

        IRentable item = Inventory.getInstance().findItemByIdAndType(rental.getItemId(), rental.getItemType());
        if (item == null) return 0;

        Member member = memberRegistry.findMemberById(rental.getMemberId());
        String level = (member != null) ? member.getMembershipLevel() : "Standard";

        IPricePolicy policy;
        double dailyRate = item.getDailyPrice();

        switch (level) {
            case "Student":
                policy = new StudentPricePolicy(dailyRate);
                break;
            case "Premium":
                policy = new PremiumPricePolicy(dailyRate);
                break;
            default:
                policy = new StandardPricePolicy(dailyRate);
                break;
        }
        return policy.calculatePrice(rental.getRentalDays());
    }

    public double calculateMemberRevenue(int memberId) {
        return rentalRegistry.getRentals().stream()
                .filter(r -> r.getMemberId() == memberId)
                .mapToDouble(this::calculateRentalRevenue)
                .sum();
    }

    public String generateMemberRevenueReport() {
        StringBuilder sb = new StringBuilder();
        for (Member member : memberRegistry.getMembers()) {
            double revenue = calculateMemberRevenue(member.getId());
            sb.append(String.format("%s %s: %.2f SEK%n",
                    member.getFirstName(),
                    member.getLastName(),
                    revenue));
        }
        return sb.toString();
    }
}