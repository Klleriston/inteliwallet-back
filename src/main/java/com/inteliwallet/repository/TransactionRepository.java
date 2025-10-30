package com.inteliwallet.repository;

import com.inteliwallet.entity.Transaction;
import com.inteliwallet.entity.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUserIdOrderByDateDesc(String userId);

    List<Transaction> findByUserIdAndTypeOrderByDateDesc(String userId, TransactionType type);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(
        String userId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<Transaction> findByUserIdAndCategoryOrderByDateDesc(String userId, String category);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.date DESC")
    List<Transaction> findByFilters(
        @Param("userId") String userId,
        @Param("type") TransactionType type,
        @Param("category") String category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("search") String search
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(
        @Param("userId") String userId,
        @Param("type") TransactionType type
    );

    @Query("SELECT t.category, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "GROUP BY t.category")
    List<Object[]> sumByCategory(
        @Param("userId") String userId,
        @Param("type") TransactionType type
    );

    @Query("SELECT FUNCTION('MONTH', t.date) as month, " +
           "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income, " +
           "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expenses " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.date >= :startDate " +
           "GROUP BY FUNCTION('MONTH', t.date) " +
           "ORDER BY FUNCTION('MONTH', t.date)")
    List<Object[]> getMonthlyData(
        @Param("userId") String userId,
        @Param("startDate") LocalDateTime startDate
    );

    long countByUserId(String userId);
}