package com.bank.strategies;

import com.bank.engine.AccountManager;
import com.bank.entities.Account;
import com.bank.entities.Transaction;

import java.util.Optional;

public class DepositStrategy implements TransactionStrategy {
    public void execute(Transaction transaction, AccountManager accountManager) {
        String accountId = transaction.getToAccountId();
        Long amount = transaction.getAmount();

        Optional<Account> accOpt = accountManager.findAccount(accountId);

        if (accOpt.isPresent()) {
            Account account = accOpt.get();
            account.getLock().lock();
            try {
                if (account.isFrozen()) {
                    System.err.printf("FAILED: Счет %s заморожен. Операция отклонена.%n", accountId);
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }
                if (account.deposit(amount)) {
                    accountManager.updateAccount(account);
                    System.out.printf("SUCCESS: Счет %s пополнен. Новый баланс: %d.%n", accountId, account.getBalance());
                    transaction.setStatus(Transaction.Status.COMPLETED);
                } else {
                    System.err.printf("FAILED: Не удалось пополнить счет %s.%n", accountId);
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } finally {
                account.getLock().unlock();
            }
        } else {
            System.err.printf("FAILED: Счет %s не найден.%n", accountId);
            transaction.setStatus(Transaction.Status.FAILED);
        }

    }
}

