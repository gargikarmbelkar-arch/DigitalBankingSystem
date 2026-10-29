package com.gargi.banking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

interface AccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByUsername(String username);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findBySenderAccountOrReceiverAccountOrderByCreatedAtDesc(String sender, String receiver);
}
interface NotificationRepository extends JpaRepository<BankNotification, Long> {
    List<BankNotification> findByUsernameOrderByCreatedAtDesc(String username);
}
