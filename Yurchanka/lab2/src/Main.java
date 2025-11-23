import com.bank.engine.DataBaseConnection;
import com.bank.entities.*;
import com.bank.Banking;
import com.bank.factory.TransactionFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Инициализация банкинга...");
        Banking app = Banking.getInstance(); // банк уникален, но имитируем запросы с разных устройств

        System.out.println("Регистрация пользователей...");
        User user1 = new User("Bill Gates", "USER_GATES");
        User user2 = new User("Elon Musk", "USER_MUSK");
        app.registerUser(user1);
        app.registerUser(user2);

        List<User> allUsers = app.getAllUsers();
        for (User user : allUsers) {
            System.out.println(user.toString());
        }

        System.out.println("Создание счетов пользователей...");
        app.createAccount("ACC_GATES_001","USER_GATES");
        app.createAccount("ACC_GATES_002", "USER_GATES");
        app.createAccount("ACC_MUSK_001", "USER_MUSK");

        List<Account> allAccounts = app.getAllAccounts();

        for (Account acc : allAccounts) {
            System.out.println(acc.toString());
        }


        // Выполняем определенную последовательность транзакций
        app.deposit("ACC_GATES_001", 1000L);
        Thread.sleep(100);
        app.deposit("ACC_GATES_002", 2000L);
        Thread.sleep(100);
        app.transfer("ACC_GATES_001", "ACC_MUSK_001", 500L);
        Thread.sleep(100);
        app.withdraw("ACC_GATES_001", 100L);
        Thread.sleep(100);
        app.freeze("ACC_GATES_002");
        Thread.sleep(100);
        app.withdraw("ACC_GATES_002", 500L); // ошибка -- счет заморожен
        Thread.sleep(100);
        app.withdraw("ACC_MUSK_001", 2000L); // ошибка -- недостаточно средств

        System.out.println("Ожидание завершения обычных транзакций...");
        Thread.sleep(1000); // Даем транзакциям поработать

        System.out.println();
        System.out.println("Состояния аккаунтов после транзакций...");
        allAccounts = app.getAllAccounts();

        for (Account acc : allAccounts) {
            System.out.println(acc.toString());
        }
        System.out.println();

        List<Transaction> requests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            requests.add(TransactionFactory.createTransfer("ACC_GATES_001", "ACC_MUSK_001", 10));
            requests.add(TransactionFactory.createTransfer("ACC_MUSK_001", "ACC_GATES_001", 10));
        }
        for (Transaction tx : requests) {
            app.submitTransaction(tx); // для демонстрации сделано public
        }

        System.out.println("Ожидание завершения транзакций...");
        Thread.sleep(1000);

        List<Transaction> transactionsHistory = app.getAllTransactions();
        System.out.println("Лог транзакций:");
        for (Transaction tx : transactionsHistory) {
            System.out.println(tx.toString());
        }

        System.out.println("Завершение работы приложения...");
        app.shutdown();
    }
}