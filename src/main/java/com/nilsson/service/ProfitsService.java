package com.nilsson.service;

import com.nilsson.entity.DailyProfit;
import com.nilsson.entity.Rental;
import com.nilsson.repo.DailyProfitRepository;
import com.nilsson.repo.RentalRepository;
import com.nilsson.repo.MemberRepository;
import com.nilsson.entity.Member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ProfitsService {

    private final RentalRepository rentalRepo;
    private final DailyProfitRepository profitRepo;
    private final MemberRepository memberRepo;

    public ProfitsService(RentalRepository rentalRepo,
                          DailyProfitRepository profitRepo,
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
        // 1. Fetch returned rentals
        List<Rental> finishedRentals = rentalRepo.getAllRentals().stream()
                .filter(r -> r.getEndTime() != null && r.getTotalCost() != null)
                .collect(Collectors.toList());

        // 2. Aggregate Income by Date using BigDecimal
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

        // 3. Update DB records
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

    public String generateMemberRevenueReport() {
        List<Member> members = memberRepo.getAllMembers();
        List<Rental> allRentals = rentalRepo.getAllRentals();

        StringBuilder sb = new StringBuilder();
        sb.append("--- Member Revenue Report ---\n");

        for (Member member : members) {
            BigDecimal memberTotal = allRentals.stream()
                    .filter(r -> r.getMember().getId().equals(member.getId()))
                    .filter(r -> r.getTotalCost() != null)
                    .map(Rental::getTotalCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (memberTotal.compareTo(BigDecimal.ZERO) > 0) {
                sb.append(String.format("ID %d - %s %s: %s SEK%n",
                        member.getId(),
                        member.getFirstName(),
                        member.getLastName(),
                        memberTotal.toPlainString()));
            }
        }
        return sb.toString();
    }
}