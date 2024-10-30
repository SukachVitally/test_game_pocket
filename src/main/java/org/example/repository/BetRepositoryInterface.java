package org.example.repository;

import java.util.Optional;
import org.example.entity.BetEntity;

public interface BetRepositoryInterface {
    BetEntity save(BetEntity bet);
    Optional<BetEntity> findById(Long betId);
}
