package com.bank.commands;

import com.bank.engine.TransactionProcessor;
import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;

public class TransactionCommand implements Command {
    private final Transaction tx;
    private final TransactionProcessor processor;

    public TransactionCommand(Transaction tx, TransactionProcessor processor) {
        if (tx == null || processor == null)
            throw new IllegalArgumentException("[TX_COMMAND] Ошибка: tx & processor != null.");
        this.tx = tx;
        this.processor = processor;
    }

    @Override
    public void execute() {
        System.out.println("[TX_COMMAND]: Отправка процессору комманды на транзакцию " + tx.getUUID());
        processor.process(tx);
    }
}
