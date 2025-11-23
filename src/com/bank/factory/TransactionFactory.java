package com.bank.factory;

import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;

public final class TransactionFactory {
    private TransactionFactory() {}

    public static Transaction createDeposit(String accountId, Long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        return new Transaction(TransactionType.DEPOSIT, accountId, amount);
    }

    public static Transaction createWithdrawal(String accountId, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Сумма для вывода должна быть положительной.");
        return new Transaction(TransactionType.WITHDRAW, accountId, amount);
    }

    public static Transaction createTransfer(String fromAccountId, String toAccountId, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Сумма для перевода должна быть положительной.");
        if (fromAccountId == null || toAccountId == null || fromAccountId.equals(toAccountId))
            throw new IllegalArgumentException("Счет отправителя и получателя не могут совпадать или быть null.");

        return new Transaction(TransactionType.TRANSFER, fromAccountId, toAccountId, amount);
    }

    public static Transaction createFreeze(String accountId) {
        return new Transaction(TransactionType.FREEZE, accountId, 0L);
    }
}