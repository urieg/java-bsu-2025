package com.bank.strategies;

import com.bank.engine.AccountManager;
import com.bank.entities.Transaction;

public interface TransactionStrategy {
    void execute(Transaction transaction, AccountManager accountManager);
}