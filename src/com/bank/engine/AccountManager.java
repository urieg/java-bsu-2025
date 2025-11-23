package com.bank.engine;

import com.bank.entities.Account;
import com.bank.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountManager {
    private final Connection connection;
    private final UserManager userManager;
    public AccountManager(Connection connection, UserManager userManager) {
        this.connection = connection;
        this.userManager = userManager;
    }

    public void createAccount(String userId) {
        String accountId = UUID.randomUUID().toString();
        createAccount(accountId, userId);
    }

    public void createAccount(String accountId, String userId) {
        if (accountId == null || userId == null)
            throw new IllegalArgumentException("[ACCOUNT_MANAGER] Ошибка создания счета: пустые id.");
        if (!userManager.userExists(userId))
            throw new IllegalArgumentException("[ACCOUNT_MANAGER] Попытка завести счет незарегистрированного пользователя.");

        String sql1 = "INSERT INTO accounts (uuid, balance, isFrozen) VALUES (?, 0, false)";
        String sql2 = "INSERT INTO user_accounts (userUUID, accountUUID) VALUES (?, ?)";
        try (PreparedStatement st1 = connection.prepareStatement(sql1);
             PreparedStatement st2 = connection.prepareStatement(sql2)) {

            st1.setString(1, accountId);
            st1.executeUpdate();

            st2.setString(1, userId);
            st2.setString(2, accountId);
            st2.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[ACCOUNT_MANAGER] Ошибка при создании счета: " + e.getMessage());
        }
    }

    public void saveAccount(Account account) {
        String sql = "MERGE INTO accounts (uuid , balance, isFrozen) KEY(uuid) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getId());
            statement.setLong(2, account.getBalance());
            statement.setBoolean(3, account.isFrozen());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[ACCOUNT_MANAGER] Ошибка: Не удалось сохранить счет " + account.getId(), e);
        }
    }



    public Optional<Account> findAccount(String accountId) {
        String sql = "SELECT * FROM accounts WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            try (ResultSet rs = statement.executeQuery()) {if (rs.next()) {
                    Account account = new Account(
                            rs.getString("uuid"),
                            rs.getLong("balance"),
                            rs.getBoolean("isFrozen")
                    );
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("[ACCOUNT_MANAGER] Ошибка: при поиске счета " + accountId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void updateAccount(Account account) {
        saveAccount(account);
    }

    public List<Account> getAllAccounts() {
        String sql = "SELECT uuid, balance, isFrozen FROM accounts";
        List<Account> accounts = new ArrayList<>();

        try {
            if (connection.isClosed()) {
                System.out.println("CLOSED");
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    Long balance = rs.getLong("balance");
                    Boolean isFrozen = rs.getBoolean("isFrozen");
                    accounts.add(new Account(uuid, balance, isFrozen));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("[ACCOUNT_MANAGER] Ошибка: " + e.getMessage());
        }

        return accounts;
    }


}
