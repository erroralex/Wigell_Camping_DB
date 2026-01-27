package com.nilsson.service;

import com.nilsson.entity.DailyProfit;
import com.nilsson.entity.Rental;
import com.nilsson.repo.ProfitRepository;
import com.nilsson.repo.RentalRepository;
import com.nilsson.repo.MemberRepository;
import com.nilsson.entity.Member;
import com.nilsson.util.LanguageManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ProfitsService {

    private final RentalRepository rentalRepo;
    private final ProfitRepository profitRepo;
    private final MemberRepository memberRepo;

    public ProfitsService(RentalRepository rentalRepo,
                          ProfitRepository profitRepo,
                          MemberRepository memberRepo) {
        this.rentalRepo = rentalRepo;
        this.profitRepo = profitRepo;
        this.memberRepo = memberRepo;
    }

    public List<DailyProfit> getAllDailyProfits() {
        List<DailyProfit> profits = profitRepo.findAll();
        profits.sort(Comparator.comparing(DailyProfit::getDate));
        return profits;
    }

    public void recalculateProfits() {
        // Fetch returned rentals
        List<Rental> finishedRentals = rentalRepo.getAllRentals().stream()
                .filter(r -> r.getEndTime() != null && r.getTotalCost() != null)
                .collect(Collectors.toList());

        Map<LocalDate, BigDecimal> incomeMap = new HashMap<>();

        for (Rental r : finishedRentals) {
            LocalDate date = r.getEndTime().toLocalDate();
            BigDecimal cost = r.getTotalCost();

            if (incomeMap.containsKey(date)) {
                incomeMap.put(date, incomeMap.get(date).add(cost));
            } else {
                incomeMap.put(date, cost);
            }
        }

        // Update DB
        List<DailyProfit> currentDbRecords = profitRepo.findAll();
        Map<LocalDate, DailyProfit> dbMap = currentDbRecords.stream()
                .collect(Collectors.toMap(DailyProfit::getDate, p -> p));

        List<DailyProfit> toSave = new ArrayList<>();

        for (Map.Entry<LocalDate, BigDecimal> entry : incomeMap.entrySet()) {
            LocalDate date = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (dbMap.containsKey(date)) {
                // Update existing
                DailyProfit p = dbMap.get(date);
                p.setIncome(amount); // Sets BigDecimal
                toSave.add(p);
            } else {
                // Create new
                DailyProfit p = new DailyProfit(date, amount);
                toSave.add(p);
            }
        }

        profitRepo.saveAll(toSave);
    }

    public BigDecimal calculateTotalIncome() {
        return rentalRepo.getAllRentals().stream()
                .filter(r -> r.getTotalCost() != null)
                .map(Rental::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getIncomeToday() {
        LocalDate today = LocalDate.now();
        DailyProfit profit = profitRepo.findByDate(today);
        return (profit != null) ? profit.getIncome() : BigDecimal.ZERO;
    }

    public String generateMemberRevenueReport(Member member) {
        List<Rental> allRentals = rentalRepo.getAllRentals();

        BigDecimal memberTotal = allRentals.stream()
                .filter(r -> r.getMember().getId().equals(member.getId()))
                .filter(r -> r.getTotalCost() != null)
                .filter(r -> r.getEndTime() != null)
                .map(Rental::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.getInstance().getString("txt.memberRevenue") + "\n\n");
        sb.append(String.format(LanguageManager.getInstance().getString("txt.member")
                + ": %s %s (ID: %d)%n", member.getFirstName(), member.getLastName(), member.getId()));
        sb.append(String.format(LanguageManager.getInstance().getString("txt.memberLevel")
                + ": %s%n", member.getMembershipLevel()));
        sb.append("---------------------------------\n");

        List<Rental> memberRentals = allRentals.stream()
                .filter(r -> r.getMember().getId().equals(member.getId()))
                .filter(r -> r.getTotalCost() != null)
                .filter(r -> r.getEndTime() != null)
                .toList();

        if (memberRentals.isEmpty()) {
            sb.append(LanguageManager.getInstance().getString("txt.noRentalsRecorded") + "\n");
        } else {
            for (Rental r : memberRentals) {
                sb.append(String.format(LanguageManager.getInstance().getString("txt.rental")
                                + " ID %d: %,.2f SEK (%s)%n",
                        r.getId(), r.getTotalCost(), r.getEndTime().toLocalDate()));
            }
        }

        sb.append("---------------------------------\n");
        sb.append(String.format(LanguageManager.getInstance().getString("txt.totalRevenue")
                + ": %,.2f SEK%n", memberTotal));

        return sb.toString();
    }
}