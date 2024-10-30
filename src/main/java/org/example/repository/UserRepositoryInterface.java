package org.example.repository;

import java.util.Optional;
import org.example.entity.UserEntity;

public interface UserRepositoryInterface {
    Optional<UserEntity> findById(Integer id);
}
