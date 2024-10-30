package org.example.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.contants.MoneyCurrencyType;

@Builder
@Getter
public class Money {
    private MoneyCurrencyType currency;
    private Double amount;
}
