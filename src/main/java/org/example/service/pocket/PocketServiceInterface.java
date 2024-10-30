package org.example.service.pocket;

import java.util.List;
import org.example.contants.PocketType;
import org.example.dto.Money;

public interface PocketServiceInterface {
    void addDeposit(Money money, Long userId);

    void bet(Money betMoney, Long userId, Long betId);

    void win(Long betId, Money money);

    List<Money> getBalance(Long userId);

    void withdraw(Long userId, Money money);

    void refund(Long betId);

    boolean supportPocket(PocketType pocketType);

    boolean supportBetId(Long betId);
}
