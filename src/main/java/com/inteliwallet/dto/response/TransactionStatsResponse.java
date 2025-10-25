package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatsResponse {
    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal savingsRate = BigDecimal.ZERO;
    private List<MonthlyData> monthlyData = new ArrayList<>();
    private List<CategoryData> categoryData = new ArrayList<>();
    private List<WeeklySpending> weeklySpending = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyData {
        private String month;
        private BigDecimal income = BigDecimal.ZERO;
        private BigDecimal expenses = BigDecimal.ZERO;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryData {
        private String name;
        private BigDecimal value = BigDecimal.ZERO;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklySpending {
        private String day;
        private BigDecimal amount = BigDecimal.ZERO;
    }
}
