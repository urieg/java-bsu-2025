package com.bank.engine;

import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;
import com.bank.observers.Observer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private final Map<String, List<Observer>> subscribers;

    public EventManager() {
        this.subscribers = new ConcurrentHashMap<>();
    }

    public void subscribe(String accountId, Observer observer) {
        subscribers
                .computeIfAbsent(accountId, k -> new CopyOnWriteArrayList<>())
                .add(observer);
        System.out.println("[EVENT_MANAGER] Добавлен наблюдатель за счетом " + accountId);
    }

    public void unsubscribe(String accountId, Observer observer) {
        List<Observer> observers = subscribers.get(accountId);
        if (observers != null) {
            observers.remove(observer);
            System.out.println("[EVENT_MANAGER] Удален наблюдатель за счетом " + accountId);

            if (observers.isEmpty()) {
                subscribers.remove(accountId);
            }
        }
    }

    public void  unsubscribeFromAll(Observer observer) {
        Iterator<Map.Entry<String, List<Observer>>> iterator =
                subscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Observer>> entry = iterator.next();
            List<Observer> observers = entry.getValue();

            observers.remove(observer);

            if (observers.isEmpty()) {
                iterator.remove();
            }
        }
        System.out.println("[EVENT_MANAGER] Наблюдатель прекратил наблюдение за счетами.");
    }

    public void notify(Transaction tx) {
        if (tx.getStatus() != Transaction.Status.COMPLETED) return;
        List<String> accounts = new ArrayList<>();
        if (tx.getAction() == TransactionType.TRANSFER) {
            accounts.add(tx.getToAccountId());
            accounts.add(tx.getFromAccountId());
        } else {
            accounts.add(tx.getFromAccountId());
        }
        for (String accId : accounts) {
            List<Observer> observers = subscribers.get(accId);
            if (observers != null && !observers.isEmpty()) {
                for (Observer observer : observers) {
                    try {
                        observer.update(tx);
                    } catch (Exception e) {
                        System.err.println("[EVENT_MANAGER] Ошибка: при апдейте наблюдателей: " + e.getMessage());
                    }
                }
            }
        }
    }

}
