package com.gargi.banking;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(unique = true, nullable = false) public String accountNumber;
    public String holderName;
    public String username;
    public String password; // DEMO ONLY: plain text is not safe for production
    public String accountType = "SAVINGS";
    public String status = "ACTIVE";
    public double balance;
    public BankAccount() {}
    public BankAccount(String number, String name, String username, String password, double balance) {
        this.accountNumber=number; this.holderName=name; this.username=username; this.password=password; this.balance=balance;
    }
}

@Entity
class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String reference;
    public String senderAccount;
    public String receiverAccount;
    public String receiverName;
    public double amount;
    public String status;
    public String description;
    public boolean suspicious;
    public LocalDateTime createdAt = LocalDateTime.now();
    public BankTransaction() {}
}

@Entity
class BankNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String username;
    public String title;
    @Column(length=1000) public String message;
    public String type;
    public LocalDateTime createdAt = LocalDateTime.now();
    public BankNotification() {}
    public BankNotification(String username, String title, String message, String type) {
        this.username=username; this.title=title; this.message=message; this.type=type;
    }
}
