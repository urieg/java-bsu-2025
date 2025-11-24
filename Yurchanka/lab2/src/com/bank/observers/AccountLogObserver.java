package com.bank.observers;

import com.bank.entities.Account;
import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;
import com.bank.visitors.AccountObserverVisitor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountLogObserver implements Observer{
    private final String accountId;
    private final List<String> timestamps = new ArrayList<>();
    private final List<Long> deltas = new ArrayList<>();

    public AccountLogObserver(Account account) {
        this.accountId = account.getId();
    }

    public AccountLogObserver(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public void update(Transaction tx) {
        if (tx.getStatus() == Transaction.Status.FAILED) {
            System.err.printf("[ACCOUNT_OBSERVER] Trying to log failed transaction  " + tx.getUUID() + "%n");
            return;
        }
        Long delta = 0L;
        String timestamp = tx.getFormattedTimestamp();

        if (tx.getAction() == TransactionType.DEPOSIT) {
            delta = tx.getAmount();
        } else if (tx.getAction() == TransactionType.WITHDRAW) {
            delta = -tx.getAmount();
        } else if (tx.getAction() == TransactionType.TRANSFER) {
            delta = tx.getAmount() * (tx.getFromAccountId() == accountId ? -1: 1);
        } // if FREEZE => delta = 0.
        deltas.add(delta);
        timestamps.add(timestamp);
    }

    public void accept(AccountObserverVisitor visitor) {
        visitor.visitAccountLogObserver(this);
    }

    public String getAccountId() { return accountId; }
    public List<String> getTimestamps() { return timestamps; }
    public List<Long> getDeltas() { return deltas; }
}
