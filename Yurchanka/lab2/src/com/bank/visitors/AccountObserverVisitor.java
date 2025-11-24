package com.bank.visitors;

import com.bank.observers.AccountLogObserver;

public interface AccountObserverVisitor {
    void visitAccountLogObserver(AccountLogObserver observer);
}
