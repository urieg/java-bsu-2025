package com.bank.entities;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.bank.entities.TransactionType;

public class Transaction {
    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }

    private final String uuid;
    private final LocalDateTime timestamp;
    private final TransactionType action;
    private final String fromAccount;
    private final String toAccount;
    private final Long amount;

    private volatile Status status;

    public Transaction(TransactionType action, String accountUUID, Long amount) {
        this(action, accountUUID, null, amount);
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(TransactionType action, String fromAccountUUID, String toAccountUUID, Long amount) {
        if (action == null)
            throw new IllegalArgumentException("Тип транзакции должен быть определен.");
        if (amount < 0)
            throw new IllegalArgumentException("Сумма транзакции должна быть неотрицательной.");

        if (action == TransactionType.WITHDRAW && fromAccountUUID == null)
            throw new IllegalArgumentException("Для вывода средств нужно указать счет.");
        if (action == TransactionType.DEPOSIT && fromAccountUUID == null)
            throw new IllegalArgumentException("Для пополнения нужно указать счет.");
        if (action == TransactionType.FREEZE && fromAccountUUID == null)
            throw new IllegalArgumentException("Для заморозки укажите целевой счет.");
        if (action == TransactionType.TRANSFER && (toAccountUUID == null || fromAccountUUID == null))
            throw new IllegalArgumentException("Для перевода должны быть указаны счет отправителя и счет получателя.");

        if (toAccountUUID == null) toAccountUUID = fromAccountUUID; // если не TRANSFER, то работа с одним счетом

        this.uuid = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.fromAccount = fromAccountUUID;
        this.toAccount = toAccountUUID;
        this.amount = amount;
        this.status = Status.PENDING;
    }

    public Transaction(String txId, LocalDateTime timestamp, TransactionType action, String toAccount, String fromAccount, Long amount, Status status) {
        this.uuid = txId;
        this.timestamp = timestamp;
        this.action = action;
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
            "TRANSACTION[id=%s, action=%s, fromAccountId=%s%s, amount=%d, timestamp=%s, status=%s]",
                uuid,
                action,
                fromAccount,
                (action == TransactionType.TRANSFER ? ", toAccountId=" + toAccount + "," : ""),
                amount,
                timestamp.toString(),
                status
        );
    }

    public String getUUID() { return uuid; }
    public String getFromAccountId() { return fromAccount; }
    public String getToAccountId() { return toAccount; }
    public TransactionType getAction() { return action; }
    public Long getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFormattedTimestamp() { return timestamp.format(FORMATTER); }
    public Status getStatus() { return status; }
    public void setStatus(Status newStatus) {this.status = newStatus; }
}
