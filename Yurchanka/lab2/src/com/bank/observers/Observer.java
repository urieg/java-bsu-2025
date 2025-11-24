package com.bank.observers;

import com.bank.entities.Transaction;

public interface Observer {
    void update(Transaction tx);
}
