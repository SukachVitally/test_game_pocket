package org.example.repository;

import java.util.Optional;
import org.example.contants.PocketTransactionType;
import org.example.entity.PocketTransactionEntity;

public interface PocketTransactionRepository {
    Boolean hasTransactions(Long userId, PocketTransactionType type);

    PocketTransactionEntity save(PocketTransactionEntity entity);

    Optional<PocketTransactionEntity> findById(Long transactionId);
}
