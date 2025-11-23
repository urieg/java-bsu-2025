package com.bank.engine;

import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;
import com.bank.strategies.*;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;
    private final Map<TransactionType, TransactionStrategy> strategies;
    private final ExecutorService executorService;

    public TransactionProcessor(AccountManager accountManager, TransactionManager transactionManager) {
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;

        int threads = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(threads);
        System.out.printf("[PROCESSOR] Запущен обработчик с пулом на %d потоков.%n", threads);

        this.strategies = Map.of(
                TransactionType.DEPOSIT, new DepositStrategy(),
                TransactionType.WITHDRAW, new WithdrawStrategy(),
                TransactionType.TRANSFER, new TransferStrategy(),
                TransactionType.FREEZE, new FreezeStrategy()
        );
    }

    public void process(Transaction transaction) {
        executorService.submit(() -> {
            try {
                TransactionStrategy strategy = strategies.get(transaction.getAction());
                if (strategy != null) {
                    strategy.execute(transaction, accountManager);
                } else {
                    System.err.println("[PROCESSOR] Ошибка: не найдена стратегия для " + transaction.getAction());
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } catch (Exception e) {
                System.err.println("[PROCESSOR] Ошибка при обработке транзакции: " + transaction.getUUID() + e.getMessage());
                transaction.setStatus(Transaction.Status.FAILED);
            } finally {
                transactionManager.saveTransaction(transaction);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("[PROCESSOR] Обработчик транзакций остановлен.");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
