package com.bank.engine;

import com.bank.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final Connection connection;
    public UserManager(Connection connection) {
        this.connection = connection;
    }

    public void registerUser(User user) {
        // TODO проверить, что conn есть
        if (user.getUUID() == null)
            throw new IllegalArgumentException("[USER_MANAGER] Ошибка: некорректный id пользователя.");
        if (userExists(user.getUUID()))
            throw new IllegalArgumentException("[USER_MANAGER] Ошибка: пользователь уже зарегистрирован.");

        String sql = "INSERT INTO users (uuid, nickname) VALUES (?, ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, user.getUUID());
            st.setString(2, user.getNickname());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[USER_MANAGER] Ошибка регистрации пользователя: " + e.getMessage());
        }
    }

    public boolean userExists(String userId) {
        // TODO проверить, что conn есть
        if (userId == null)
            throw new IllegalArgumentException("[USER_MANAGER] Ошибка: некорректный id пользователя.");

        String sql = "SELECT 1 FROM users WHERE uuid = ? LIMIT 1";
        try(PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, userId);
            try(ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("[USER_MANAGER] Ошибка при поиске пользователя: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT uuid, nickname FROM users";
        List<User> users = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    String nickname = rs.getString("nickname");
                    users.add(new User(nickname, uuid));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("[USER_MANAGER] Ошибка: " + e.getMessage());
        }

        return users;
    }
}
