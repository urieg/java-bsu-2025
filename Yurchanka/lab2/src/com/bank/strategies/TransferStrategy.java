package com.bank.strategies;

import com.bank.engine.AccountManager;
import com.bank.entities.Account;
import com.bank.entities.Transaction;

import java.util.Optional;

public class TransferStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountManager accountManager) {
        String fromAccountId = transaction.getFromAccountId();
        String toAccountId = transaction.getToAccountId();
        Long amount = transaction.getAmount();

        Optional<Account> fromOpt = accountManager.findAccount(fromAccountId);
        Optional<Account> toOpt = accountManager.findAccount(toAccountId);

        if (fromOpt.isPresent() && toOpt.isPresent()) {
            Account from = fromOpt.get();
            Account to = toOpt.get();

            from.getLock().lock();
            to.getLock().lock();
            try {
                if (from.isFrozen() || to.isFrozen()) {
                    System.err.println("FAILED: Один из счетов заморожен.");
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }

                if (from.withdraw(amount)) {
                    to.deposit(amount);

                    accountManager.updateAccount(from);
                    accountManager.updateAccount(to);

                    transaction.setStatus(Transaction.Status.COMPLETED);
                    System.out.printf("SUCCESS: Перевод выполнен %s -> %s, amount=%d.%n", fromAccountId, toAccountId, amount);
                } else {
                    System.err.println("FAILED: Недостаточно средств.");
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } finally {
                to.getLock().unlock();
                from.getLock().unlock();
            }
        } else {
            System.err.println("FAILED: Один из счетов не найден.");
            transaction.setStatus(Transaction.Status.FAILED);
        }
    }
}
