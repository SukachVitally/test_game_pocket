package org.example.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.contants.PocketTransactionType;

@Builder
@Getter
public class PocketTransactionEntity {
    private Long id;
    private PocketTransactionType type;
    private Long userId;
    private LocalDateTime createdAt;
}
