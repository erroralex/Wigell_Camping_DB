package com.nilsson.repo;

import com.nilsson.entity.DailyProfit;
import java.time.LocalDate;
import java.util.List;

public interface ProfitRepository {
    void save(DailyProfit profit);
    void saveAll(List<DailyProfit> profits);
    List<DailyProfit> GetAllProfits();
    DailyProfit findByDate(LocalDate date);
    void deleteAll();
}
