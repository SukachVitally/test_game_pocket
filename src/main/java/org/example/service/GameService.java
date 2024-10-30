package org.example.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.contants.PocketType;
import org.example.dto.Money;
import org.example.entity.BetEntity;
import org.example.entity.UserEntity;
import org.example.exception.BetNotFoundException;
import org.example.repository.BetRepositoryInterface;
import org.example.service.pocket.PocketServiceInterface;

@RequiredArgsConstructor
public class GameService {
    private final List<PocketServiceInterface> pocketServices;
    private final BetRepositoryInterface betRepository;

    public void addDeposit(UserEntity user, Money money, PocketType pocketType) {
        pocketServices.stream()
                .filter(p -> p.supportPocket(pocketType))
                .forEach(p -> p.addDeposit(money, user.getId()));
    }

    public Long bet(UserEntity user, Money money, PocketType pocketType) {
        var bet = betRepository.save(BetEntity.builder()
                        .bet(money)
                        .createdAt(LocalDateTime.now())
                        .userId(user.getId())
                .build());
        pocketServices.stream()
                .filter(p -> p.supportPocket(pocketType))
                .forEach(p -> p.bet(money, user.getId(), bet.getId()));
        return bet.getId();
    }

    public void win(Long betId, Money money) {
        var bet = betRepository.findById(betId).orElseThrow(BetNotFoundException::new);
        bet.setWin(money);
        bet.setCompletedAt(LocalDateTime.now());

        pocketServices.stream()
                .filter(p -> p.supportBetId(betId))
                .forEach(p -> p.win(betId, money));
    }

    public void withdraw(UserEntity user, Money money, PocketType pocketType) {
        pocketServices.stream()
                .filter(p -> p.supportPocket(pocketType))
                .forEach(p -> p.withdraw(user.getId(), money));
    }

    public void refund(Long betId) {
        pocketServices.stream()
                .filter(p -> p.supportBetId(betId))
                .forEach(p -> p.refund(betId));
    }
}
