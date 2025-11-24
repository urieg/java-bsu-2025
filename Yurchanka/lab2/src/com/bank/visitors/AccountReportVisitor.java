package com.bank.visitors;

import com.bank.observers.AccountLogObserver;

import java.util.List;

public class AccountReportVisitor implements AccountObserverVisitor{
    private String report = null;

    @Override
    public void visitAccountLogObserver(AccountLogObserver observer) {
        List<Long> deltas = observer.getDeltas();
        List<String> timestamps = observer.getTimestamps();
        String accId = observer.getAccountId();
        report = "=======Выписка по счету " + accId + "=======\n";
        for (int j = deltas.size() - 1; j >= 0; j--) {
            if (deltas.get(j) == 0) {
                report += String.format("%s Account frozen.%n", timestamps.get(j));
            } else {
                report += String.format("%s %s BYN%n", timestamps.get(j), (deltas.get(j) > 0 ? "+" : "") + deltas.get(j).toString());
            }
        }
    }

    public String getReport() {
        return report;
    }
}
