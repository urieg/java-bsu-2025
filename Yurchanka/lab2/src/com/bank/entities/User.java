package com.bank.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final String uuid;
    private final String nickname;
    List<Account> accounts = new ArrayList<>();

    public User(String nickname) {
        this.nickname = nickname;
        this.uuid = UUID.randomUUID().toString();
    }

    public User(String nickname, String uuid) {
        this.nickname = nickname;
        this.uuid = uuid;
    }

    public void addAccount(Account acc) { accounts.add(acc); }

    public String getUUID() { return uuid; }
    public String getNickname() { return nickname; }
    public List<Account> getAccounts() { return accounts; }

    @Override
    public String toString() {
        String accStr = "";
        for (Account acc : accounts) {
            if (accStr != "") {
                accStr += ", ";
            }
            accStr += acc.getId();
        }
        return String.format(
                "USER[id=%s, name='%s', accounts=[%s]]",
                uuid,
                nickname,
                accStr
        );
    }
}
