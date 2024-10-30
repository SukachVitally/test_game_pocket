package org.example.repository;

import java.util.List;
import org.example.contants.PocketType;
import org.example.dto.Money;
import org.example.entity.PocketEntity;

public interface PocketRepositoryInterface {
    PocketEntity save(PocketEntity entity);
    List<Money> getBalance(Long userId, List<PocketType> types);
    List<PocketEntity> findAllByTransactionId(Long transactionId);
}
