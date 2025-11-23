package com.bank.strategies;

import com.bank.engine.AccountManager;
import com.bank.entities.Account;
import com.bank.entities.Transaction;

import java.util.Optional;

public class FreezeStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountManager accountManager) {
        String accountId = transaction.getFromAccountId();

        Optional<Account> accountOpt = accountManager.findAccount(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.getLock().lock();
            try {
                account.setFrozen(true);
                accountManager.updateAccount(account);
                System.out.printf("SUCCESS: Счет %s заморожен.%n", accountId);
                transaction.setStatus(Transaction.Status.COMPLETED);
            } finally {
                account.getLock().unlock();
            }
        } else {
            System.err.printf("FAILED: Не удалось заморозить счет %s.%n", accountId);
            transaction.setStatus(Transaction.Status.FAILED);
        }
    }
}
