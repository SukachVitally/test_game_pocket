package org.example.service.pocket;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.contants.MoneyCurrencyType;
import org.example.contants.PocketTransactionType;
import org.example.contants.PocketType;
import org.example.dto.Money;
import org.example.entity.BetTransactionEntity;
import org.example.entity.PocketEntity;
import org.example.entity.PocketTransactionEntity;
import org.example.exception.BetNotFoundException;
import org.example.exception.BetTransactionNotFoundException;
import org.example.exception.InsufficientMoneyBalanceException;
import org.example.repository.BetTransactionRepositoryInterface;
import org.example.repository.PocketRepositoryInterface;
import org.example.repository.PocketTransactionRepository;

@RequiredArgsConstructor
public class BonusPocketService implements PocketServiceInterface {
    private final PocketRepositoryInterface pocketRepository;
    private final PocketTransactionRepository transactionRepository;
    private final BetTransactionRepositoryInterface betTransactionRepository;

    @Override
    public void addDeposit(Money money, Long userId) {
        addFreezeBalance(money, userId);
        if (isFirstDeposit(userId)) {
            addBonusBalance(money, userId);
        }
    }

    @Override
    public void bet(Money betMoney, Long userId, Long betId) {
        Money freezeBalance = getFreezeBalance(userId, betMoney.getCurrency());
        Money bonusBalance = getBonusBalance(userId, betMoney.getCurrency());
        if (freezeBalance.getAmount() + bonusBalance.getAmount() < betMoney.getAmount()) {
            throw new InsufficientMoneyBalanceException();
        }
        PocketTransactionEntity transaction = transactionRepository.save(PocketTransactionEntity.builder()
                .userId(userId)
                .type(freezeBalance.getAmount() >= betMoney.getAmount()
                        ? PocketTransactionType.BET
                        : PocketTransactionType.BONUS_BET)
                .createdAt(LocalDateTime.now())
                .build());

        if (freezeBalance.getAmount() >= betMoney.getAmount()) {
            pocketRepository.save(PocketEntity.builder()
                            .transactionId(transaction.getId())
                            .money(Money.builder()
                                    .amount(-betMoney.getAmount())
                                    .currency(betMoney.getCurrency())
                                    .build())
                            .type(PocketType.FREEZE)
                            .userId(userId)
                    .build());
        } else {
            pocketRepository.save(PocketEntity.builder()
                    .transactionId(transaction.getId())
                    .money(Money.builder()
                            .amount(-betMoney.getAmount())
                            .currency(betMoney.getCurrency())
                            .build())
                    .type(PocketType.FREEZE)
                    .userId(userId)
                    .build());

            pocketRepository.save(PocketEntity.builder()
                    .transactionId(transaction.getId())
                    .money(Money.builder()
                            .amount(-(betMoney.getAmount() - freezeBalance.getAmount()))
                            .currency(betMoney.getCurrency())
                            .build())
                    .type(PocketType.BONUS)
                    .userId(userId)
                    .build());
        }

        betTransactionRepository.save(BetTransactionEntity.builder()
                .betId(betId)
                .transactionId(transaction.getId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public List<Money> getBalance(Long userId) {
        return List.of();
    }

    @Override
    public void withdraw(Long userId, Money money) {
        PocketTransactionEntity transaction = transactionRepository.save(PocketTransactionEntity.builder()
                .userId(userId)
                .type(PocketTransactionType.WITHDRAW)
                .createdAt(LocalDateTime.now())
                .build());

        pocketRepository.save(PocketEntity.builder()
                .transactionId(transaction.getId())
                .money(Money.builder()
                        .amount(-money.getAmount())
                        .currency(money.getCurrency())
                        .build())
                .type(PocketType.FREEZE)
                .userId(userId)
                .build());
    }

    @Override
    public void refund(Long betId) {

    }

    @Override
    public boolean supportPocket(PocketType pocketType) {
        return pocketType.equals(PocketType.BONUS) || pocketType.equals(PocketType.FREEZE);
    }

    @Override
    public boolean supportBetId(Long betId) {
        var betTransaction = betTransactionRepository.findByBetId(betId).orElseThrow(BetNotFoundException::new);
        var transaction = transactionRepository
                .findById(betTransaction.getTransactionId())
                .orElseThrow(BetTransactionNotFoundException::new);

        return !pocketRepository.findAllByTransactionId(transaction.getId()).isEmpty();
    }

    @Override
    public void win(Long betId, Money money) {
        var betTransaction = betTransactionRepository.findByBetId(betId).orElseThrow(BetNotFoundException::new);
        var transaction = transactionRepository.findById(betTransaction.getTransactionId())
                .orElseThrow(BetTransactionNotFoundException::new);

        var transactionType = transaction.getType();

        PocketTransactionEntity newTransaction = transactionRepository.save(PocketTransactionEntity.builder()
                .userId(transaction.getUserId())
                .type(transactionType.equals(PocketTransactionType.BONUS_BET) ? PocketTransactionType.BONUS_WIN : PocketTransactionType.WIN)
                .createdAt(LocalDateTime.now())
                .build());

        pocketRepository.save(PocketEntity.builder()
                .transactionId(newTransaction.getId())
                .money(money)
                .type(newTransaction.getType().equals(PocketTransactionType.BONUS_WIN) ? PocketType.BONUS : PocketType.FREEZE)
                .userId(transaction.getUserId())
                .build());

        if (isBonusPayout()) {
            PocketTransactionEntity bonusPayoutTransaction = transactionRepository.save(PocketTransactionEntity.builder()
                    .userId(transaction.getUserId())
                    .type(PocketTransactionType.BONUS_PAYOUT)
                    .createdAt(LocalDateTime.now())
                    .build());
            pocketRepository.save(PocketEntity.builder()
                    .transactionId(bonusPayoutTransaction.getId())
                    .money(money)
                    .type(PocketType.FREEZE)
                    .userId(transaction.getUserId())
                    .build());
            pocketRepository.save(PocketEntity.builder()
                    .transactionId(bonusPayoutTransaction.getId())
                    .money(Money.builder().amount(money.getAmount()).currency(money.getCurrency()).build())
                    .type(PocketType.BONUS)
                    .userId(transaction.getUserId())
                    .build());
        }

    }

    private void addFreezeBalance(Money money, Long userId) {
        PocketTransactionEntity transaction = transactionRepository.save(PocketTransactionEntity.builder()
                .type(PocketTransactionType.DEPOSIT)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());
        pocketRepository.save(PocketEntity.builder()
                .type(PocketType.FREEZE)
                .money(money)
                .transactionId(transaction.getId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void addBonusBalance(Money money, Long userId) {
        PocketTransactionEntity transaction = transactionRepository.save(PocketTransactionEntity.builder()
                .type(PocketTransactionType.BONUS_ACCRUAL)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());
        pocketRepository.save(PocketEntity.builder()
                .type(PocketType.FREEZE)
                .money(money)
                .transactionId(transaction.getId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private boolean isFirstDeposit(Long userId) {
        return !transactionRepository.hasTransactions(userId, PocketTransactionType.BONUS_ACCRUAL);
    }

    private Money getFreezeBalance(Long userId, MoneyCurrencyType currencyType) {
        return pocketRepository.getBalance(userId, PocketType.FREEZE).stream()
                .filter(money -> money.getCurrency().equals(currencyType))
                .reduce((m1, m2) -> Money.builder()
                        .amount(m1.getAmount() + m2.getAmount())
                        .currency(currencyType).build())
                .orElse(Money.builder()
                        .amount(0d)
                        .currency(currencyType)
                        .build());
    }

    private Money getBonusBalance(Long userId, MoneyCurrencyType currencyType) {
        return pocketRepository.getBalance(userId, PocketType.BONUS).stream()
                .filter(money -> money.getCurrency().equals(currencyType))
                .reduce((m1, m2) -> Money.builder()
                        .amount(m1.getAmount() + m2.getAmount())
                        .currency(currencyType).build())
                .orElse(Money.builder()
                        .amount(0d)
                        .currency(currencyType)
                        .build());
    }

    private boolean isBonusPayout() {
        return true;
    }
}
