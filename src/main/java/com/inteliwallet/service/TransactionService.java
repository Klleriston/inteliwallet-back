package com.inteliwallet.service;

import com.inteliwallet.dto.request.CreateTransactionRequest;
import com.inteliwallet.dto.request.UpdateTransactionRequest;
import com.inteliwallet.dto.response.TransactionResponse;
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

import java.time.LocalDateTime;
import java.util.List;
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

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);
        response.setType(transaction.getType().getValue());
        return response;
    }
}