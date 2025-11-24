package com.bank.engine;

import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManager {
    private final Connection connection;
    private final AccountManager accountManager;
    private final EventManager eventManager;

    public TransactionManager(Connection connection, AccountManager accountManager, EventManager eventManager) {
        if (connection == null || accountManager == null)
            throw new IllegalArgumentException("[TX_MANAGER] Ошибка пустые аргументы.");

        this.connection = connection;
        this.accountManager = accountManager;
        this.eventManager = eventManager;
    }

    public void saveTransaction(Transaction tx) {
        String sql = "MERGE INTO transactions (uuid, timestamp, action, fromAccountId, toAccountId, amount, status) KEY(uuid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        eventManager.notify(tx);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tx.getUUID());
            ps.setTimestamp(2, Timestamp.valueOf(tx.getTimestamp()));
            ps.setString(3, tx.getAction().toString());
            ps.setString(4, tx.getFromAccountId());
            ps.setString(5, tx.getToAccountId());
            ps.setLong(6, tx.getAmount());
            ps.setString(7, tx.getStatus().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[TRANSACTION_MANAGER] Ошибка: Не удалось сохранить транзакцию", e);
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction tx = new Transaction(
                        rs.getString("uuid"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        TransactionType.valueOf(rs.getString("action")),
                        rs.getString("fromAccountId"),
                        rs.getString("toAccountId"),
                        rs.getLong("amount"),
                        Transaction.Status.valueOf(rs.getString("status"))
                );
                transactions.add(tx);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
