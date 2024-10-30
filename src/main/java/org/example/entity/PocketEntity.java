package org.example.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.contants.PocketType;
import org.example.dto.Money;

@Builder
@Getter
public class PocketEntity {
    private Long id;
    private PocketType type;
    private Money money;
    private Long transactionId;
    private Long userId;
    private LocalDateTime createdAt;
}
