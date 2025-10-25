package com.inteliwallet.service;

import com.inteliwallet.dto.request.CreateTransactionRequest;
import com.inteliwallet.dto.request.UpdateTransactionRequest;
import com.inteliwallet.dto.response.TransactionResponse;
import com.inteliwallet.dto.response.TransactionStatsResponse;
import com.inteliwallet.entity.Transaction;
import com.inteliwallet.entity.Transaction.TransactionType;
import com.inteliwallet.entity.User;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.TransactionRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<TransactionResponse> listTransactions(String userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public TransactionResponse getTransaction(String userId, String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transação não encontrada");
        }

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse createTransaction(String userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(TransactionType.fromValue(request.getType()));
        transaction.setAmount(request.getAmount());
        transaction.setTitle(request.getTitle());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(String userId, String transactionId, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transação não encontrada");
        }

        if (request.getType() != null) {
            transaction.setType(TransactionType.fromValue(request.getType()));
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getTitle() != null) {
            transaction.setTitle(request.getTitle());
        }
        if (request.getCategory() != null) {
            transaction.setCategory(request.getCategory());
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(String userId, String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transação não encontrada");
        }

        transactionRepository.delete(transaction);
    }

    public TransactionStatsResponse getStats(String userId) {
        TransactionStatsResponse stats = new TransactionStatsResponse();

        List<Transaction> allTransactions = transactionRepository.findByUserIdOrderByDateDesc(userId);

        if (allTransactions.isEmpty()) {
            stats.setMonthlyData(createEmptyMonthlyData());
            stats.setWeeklySpending(createEmptyWeeklyData());
            stats.setCategoryData(new ArrayList<>());
            return stats;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Transaction> currentMonthTransactions = allTransactions.stream()
            .filter(t -> t.getDate().isAfter(startOfMonth) || t.getDate().isEqual(startOfMonth))
            .collect(Collectors.toList());

        BigDecimal totalIncome = currentMonthTransactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = currentMonthTransactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        BigDecimal savingsRate = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = balance.divide(totalIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }

        stats.setTotalIncome(totalIncome);
        stats.setTotalExpenses(totalExpenses);
        stats.setBalance(balance);
        stats.setSavingsRate(savingsRate);

        stats.setMonthlyData(calculateMonthlyData(allTransactions));

        stats.setCategoryData(calculateCategoryData(currentMonthTransactions));

        stats.setWeeklySpending(calculateWeeklySpending(allTransactions));

        return stats;
    }

    private List<TransactionStatsResponse.MonthlyData> createEmptyMonthlyData() {
        List<TransactionStatsResponse.MonthlyData> monthlyData = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

        for (String month : months) {
            monthlyData.add(new TransactionStatsResponse.MonthlyData(
                month,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            ));
        }

        return monthlyData;
    }

    private List<TransactionStatsResponse.WeeklySpending> createEmptyWeeklyData() {
        List<TransactionStatsResponse.WeeklySpending> weeklyData = new ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (String day : days) {
            weeklyData.add(new TransactionStatsResponse.WeeklySpending(day, BigDecimal.ZERO));
        }

        return weeklyData;
    }

    private List<TransactionStatsResponse.MonthlyData> calculateMonthlyData(List<Transaction> transactions) {
        LocalDateTime now = LocalDateTime.now();
        List<TransactionStatsResponse.MonthlyData> monthlyData = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

            List<Transaction> monthTransactions = transactions.stream()
                .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
                .collect(Collectors.toList());

            BigDecimal income = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expenses = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            String monthName = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            monthlyData.add(new TransactionStatsResponse.MonthlyData(monthName, income, expenses));
        }

        return monthlyData;
    }

    private List<TransactionStatsResponse.CategoryData> calculateCategoryData(List<Transaction> transactions) {
        Map<String, BigDecimal> categoryMap = new HashMap<>();

        transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .forEach(t -> {
                String category = t.getCategory();
                categoryMap.merge(category, t.getAmount(), BigDecimal::add);
            });

        String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};
        List<TransactionStatsResponse.CategoryData> categoryData = new ArrayList<>();

        int colorIndex = 0;
        for (Map.Entry<String, BigDecimal> entry : categoryMap.entrySet()) {
            String color = colors[colorIndex % colors.length];
            categoryData.add(new TransactionStatsResponse.CategoryData(
                entry.getKey(),
                entry.getValue(),
                color
            ));
            colorIndex++;
        }

        return categoryData;
    }

    private List<TransactionStatsResponse.WeeklySpending> calculateWeeklySpending(List<Transaction> transactions) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);

        List<Transaction> weekTransactions = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .filter(t -> !t.getDate().isBefore(weekStart))
            .collect(Collectors.toList());

        Map<String, BigDecimal> dailySpending = new LinkedHashMap<>();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : dayNames) {
            dailySpending.put(day, BigDecimal.ZERO);
        }

        weekTransactions.forEach(t -> {
            DayOfWeek dayOfWeek = t.getDate().getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            dailySpending.merge(dayName, t.getAmount(), BigDecimal::add);
        });

        List<TransactionStatsResponse.WeeklySpending> weeklyData = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : dailySpending.entrySet()) {
            weeklyData.add(new TransactionStatsResponse.WeeklySpending(entry.getKey(), entry.getValue()));
        }

        return weeklyData;
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);
        response.setType(transaction.getType().getValue());
        return response;
    }
}