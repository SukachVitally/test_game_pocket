package org.example.service.pocket;


import java.util.List;
import org.example.contants.PocketType;
import org.example.dto.Money;

public class FiatPocketService implements PocketServiceInterface {

    @Override
    public void addDeposit(Money money, Long userId) {

    }

    @Override
    public void bet(Money betMoney, Long userId, Long betId) {

    }

    @Override
    public void win(Long betId, Money money) {

    }

    @Override
    public List<Money> getBalance(Long userId) {
        return null;
    }

    @Override
    public void withdraw(Long userId, Money money) {

    }

    @Override
    public void refund(Long betId) {

    }

    @Override
    public boolean supportPocket(PocketType pocketType) {
        return false;
    }

    @Override
    public boolean supportBetId(Long betId) {
        return false;
    }
}
