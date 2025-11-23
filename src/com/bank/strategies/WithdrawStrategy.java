package com.bank.strategies;

import com.bank.engine.AccountManager;
import com.bank.entities.Account;
import com.bank.entities.Transaction;

import java.util.Optional;

public class WithdrawStrategy implements TransactionStrategy {
    @Override
    public void execute(Transaction transaction, AccountManager accountManager) {
        String accountId = transaction.getFromAccountId();
        long amount = transaction.getAmount();

        Optional<Account> accountOpt = accountManager.findAccount(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.getLock().lock();
            try {
                if (account.isFrozen()) {
                    System.err.printf("FAILED: Счет %s заморожен. Операция отклонена.%n", accountId);
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }

                if (account.withdraw(amount)) {
                    accountManager.updateAccount(account);
                    System.out.printf("SUCCESS: Со счета %s снято %d. Баланс: %d%n", accountId, amount, account.getBalance());
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
