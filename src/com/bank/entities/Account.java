package com.bank.entities;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String uuid;
    private final AtomicLong balance = new AtomicLong(0);
    private final AtomicBoolean isFrozen = new AtomicBoolean(false);
    private final Lock lock = new ReentrantLock();

    public Account(String uuid, Long balance, boolean isFrozen) {
        this.uuid = uuid;
        this.balance.set(balance);
        this.isFrozen.set(isFrozen);
    }

    public Account(String uuid, Long balance) {
        this.uuid = uuid;
        this.balance.set(balance);
    }

    public Account(String uuid) { this.uuid = uuid; }

    public Account(Long balance) {
        this.uuid = UUID.randomUUID().toString();
        this.balance.set(balance);
    }

    public void setFrozen(Boolean frozen) {
        this.isFrozen.set(frozen);
        System.out.println("Счет " + uuid + (frozen ? " был заморожен." : " был разморожен."));
    }

    public boolean isFrozen() { return isFrozen.get(); }
    public Long getBalance() { return balance.get(); }
    public String getId() { return uuid; }
    public Lock getLock() { return lock; }

    public boolean deposit(long amount) {
        if (isFrozen.get()) {
            System.err.println("Ошибка: Счет " + uuid + " заморожен. Пополнение невозможно.");
            return false;
        }
        if (amount < 0) {
            System.err.println("Ошибка: Сумма пополнения должна быть неотрицательной.");
            return false;
        }
        balance.addAndGet(amount);
        return true;
    }

    public boolean withdraw(long amount) {
        if (isFrozen()) {
            System.err.println("Ошибка: Счет " + uuid + " заморожен. Снятие невозможно.");
            return false;
        }
        if (amount < 0) {
            System.err.println("Ошибка: Сумма для снятия должна быть неотрицательной.");
            return false;
        }

        Long curBalance;
        do {
            curBalance = balance.get();
            if (curBalance < amount) {
                System.err.println("Ошибка: Недостаточно средств на счете " + uuid);
                return false;
            }
        } while (!balance.compareAndSet(curBalance, curBalance - amount));

        return true;
    }

    @Override
    public String toString() {
        return String.format(
            "ACCOUNT[id=%s, balance=%d, isFrozen=%b]",
                uuid,
                balance.get(),
                isFrozen.get()
        );
    }
}
