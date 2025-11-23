package com.bank.engine;

import java.sql.*;


public class DataBaseConnection {
    private static volatile DataBaseConnection instance;
    private final Connection connection;

    private static final String URL = "jdbc:h2:mem:bankdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "Admin";
    private static final String PASSWORD = "";

    private DataBaseConnection() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DBC]Получено соединение с БД.");
            InitTables();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("[DBC]JDBC драйвер не найден.");
        } catch(SQLException e) {
            throw new RuntimeException("[DBC]Ошибка во время подключения к БД.");
        }
    }

    public static DataBaseConnection getInstance() {
        synchronized (DataBaseConnection.class) {
            if (instance == null) {
                instance = new DataBaseConnection();
            }
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void InitTables() {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
            st.execute("DROP TABLE IF EXISTS accounts");
            st.execute("DROP TABLE IF EXISTS user_accounts");
            st.execute("DROP TABLE IF EXISTS transactions");

            st.execute("CREATE TABLE users (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "nickname VARCHAR(100))"
            );

            st.execute("CREATE TABLE accounts (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "balance BIGINT, " +
                    "isFrozen BOOLEAN)"
            );

            st.execute("CREATE TABLE transactions (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "timestamp TIMESTAMP, " +
                    "action VARCHAR(50), " +
                    "fromAccountId VARCHAR(36), " +
                    "toAccountId VARCHAR(36), " +
                    "amount BIGINT, " +
                    "status VARCHAR(50))"
            );

            st.execute("CREATE TABLE user_accounts (" +
                    "userUUID VARCHAR(36) NOT NULL, " +
                    "accountUUID VARCHAR(36) NOT NULL, " +
                    "PRIMARY KEY (userUUID, accountUUID), " +
                    "FOREIGN KEY (userUUID) REFERENCES users(uuid), " +
                    "FOREIGN KEY (accountUUID) REFERENCES accounts(uuid))"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DBC]Соединение с БД закрыто.");
            }
        } catch(SQLException e) {
            throw new RuntimeException("[DBC]Ошибка при закрытии соединения с БД.");
        }
    }
}
