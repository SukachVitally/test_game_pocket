package org.example.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BetTransactionEntity {
    private Long id;
    private Long betId;
    private Long transactionId;
    private LocalDateTime createdAt;
}
