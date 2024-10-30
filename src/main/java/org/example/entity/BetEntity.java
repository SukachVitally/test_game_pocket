package org.example.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.Money;

@Builder
@Getter
public class BetEntity {
    private Long id;
    private Long userId;
    private Money bet;
    @Setter
    private Money win;
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime completedAt;
}
