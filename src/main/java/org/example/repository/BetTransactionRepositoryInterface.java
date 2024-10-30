package org.example.repository;

import java.util.Optional;
import org.example.entity.BetTransactionEntity;

public interface BetTransactionRepositoryInterface {
    BetTransactionEntity save(BetTransactionEntity entity);

    Optional<BetTransactionEntity> findByBetId(Long betId);
}
