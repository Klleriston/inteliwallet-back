package com.inteliwallet.controller;

import com.inteliwallet.dto.request.CreateTransactionRequest;
import com.inteliwallet.dto.request.UpdateTransactionRequest;
import com.inteliwallet.dto.response.TransactionResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(@CurrentUser String userId) {
        return ResponseEntity.ok(transactionService.listTransactions(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
        @CurrentUser String userId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, id));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
        @CurrentUser String userId,
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.createTransaction(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
        @CurrentUser String userId,
        @PathVariable String id,
        @Valid @RequestBody UpdateTransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.updateTransaction(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
        @CurrentUser String userId,
        @PathVariable String id
    ) {
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
    }
}