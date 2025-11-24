package com.bank;

import com.bank.commands.TransactionCommand;
import com.bank.engine.*;
import com.bank.entities.*;
import com.bank.factory.TransactionFactory;
import com.bank.observers.AccountLogObserver;
import com.bank.observers.Observer;
import com.bank.visitors.AccountReportVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Banking {
    private static volatile Banking instance;
    private final DataBaseConnection dbc;
    private final Connection connection;

    private final EventManager eventManager;
    private final UserManager userManager;
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    private final TransactionProcessor processor;

    private Banking() {
        dbc = DataBaseConnection.getInstance();
        connection = dbc.getConnection();

        eventManager = new EventManager();
        userManager = new UserManager(connection);
        accountManager = new AccountManager(connection, userManager);
        transactionManager = new TransactionManager(connection, accountManager, eventManager);


        processor = new TransactionProcessor(accountManager, transactionManager);
    }

    public static Banking getInstance() {
        synchronized (Banking.class) {
            if (instance == null) {
                instance = new Banking();
            }
        }
        return instance;
    }

    public void registerUser(User user) { userManager.registerUser(user); }
    public List<User> getAllUsers() { return userManager.getAllUsers(); }


    public void createAccount(String userId) { createAccount(userId); }
    public void createAccount(String accountId, String userId) { accountManager.createAccount(accountId, userId); }
    public void saveAccount(Account account) { accountManager.saveAccount(account); }
    public void updateAccount(Account account) { accountManager.updateAccount(account); }
    public List<Account> getAllAccounts() { return accountManager.getAllAccounts(); }

    public List<Transaction> getAllTransactions() { return transactionManager.getAllTransactions(); }

    public void deposit(String accountId, Long amount) {
        Transaction tx = TransactionFactory.createDeposit(accountId, amount);
        submitTransaction(tx);
    }

    public void withdraw(String accountId, Long amount) {
        Transaction tx = TransactionFactory.createWithdrawal(accountId, amount);
        submitTransaction(tx);
    }

    public void transfer(String fromId, String toId, Long amount) {
        Transaction tx = TransactionFactory.createTransfer(fromId, toId, amount);
        submitTransaction(tx);
    }

    public void freeze(String accountId) {
        Transaction tx = TransactionFactory.createFreeze(accountId);
        submitTransaction(tx);
    }

    public void submitTransaction(Transaction tx) {
        TransactionCommand command = new TransactionCommand(tx, processor);
        command.execute();
    }

    public void setObservation(String accountId, Observer observer) {
        eventManager.subscribe(accountId, observer);
    }
    public void stopObservation(Observer observer) {
        disableObserver(observer);
    }
    public void disableObserver(Observer observer) {
        eventManager.unsubscribeFromAll(observer);
    }

    public String createReport(AccountLogObserver observer) {
        AccountReportVisitor reporter = new AccountReportVisitor();
        observer.accept(reporter);
        return reporter.getReport();
    }

    public void shutdown() {
        processor.shutdown();
        dbc.closeConnection();
    }
}
