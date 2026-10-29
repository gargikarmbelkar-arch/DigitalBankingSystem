package com.gargi.banking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BankingController {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final NotificationRepository notifications;

    public BankingController(AccountRepository accounts, TransactionRepository transactions, NotificationRepository notifications) {
        this.accounts=accounts; this.transactions=transactions; this.notifications=notifications;
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,String> body) {
        String username=body.getOrDefault("username","").trim();
        String password=body.getOrDefault("password","");
        BankAccount a=accounts.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password"));
        if (!Objects.equals(a.password,password)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
        return accountView(a);
    }

    @GetMapping("/account/{username}")
    public Map<String,Object> account(@PathVariable String username) {
        return accountView(getAccount(username));
    }

    @GetMapping("/transactions/{username}")
    public List<Map<String,Object>> history(@PathVariable String username) {
        BankAccount a=getAccount(username);
        return transactions.findBySenderAccountOrReceiverAccountOrderByCreatedAtDesc(a.accountNumber,a.accountNumber)
            .stream().map(this::transactionView).toList();
    }

    @GetMapping("/notifications/{username}")
    public List<BankNotification> notificationList(@PathVariable String username) {
        getAccount(username);
        return notifications.findByUsernameOrderByCreatedAtDesc(username);
    }

    @PostMapping("/transfer/{username}")
    @Transactional
    public Map<String,Object> transfer(@PathVariable String username, @RequestBody Map<String,String> body) {
        BankAccount sender=getAccount(username);
        if (!"ACTIVE".equals(sender.status)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This account is not active");
        String receiverNumber=body.getOrDefault("receiverAccount","").trim();
        String receiverName=body.getOrDefault("receiverName","Receiver").trim();
        String description=body.getOrDefault("description","Money transfer").trim();
        double amount;
        try { amount=Double.parseDouble(body.getOrDefault("amount","0")); }
        catch (NumberFormatException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Enter a valid amount"); }
        if (amount<=0 || !Double.isFinite(amount)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Amount must be greater than zero");
        if (receiverNumber.equals(sender.accountNumber)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot transfer money to your own account");
        if (amount>sender.balance) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient balance");

        boolean suspicious = amount >= 50000 || amount >= sender.balance * 0.50;
        BankTransaction tx=new BankTransaction();
        tx.reference="TXN"+UUID.randomUUID().toString().replace("-","").substring(0,10).toUpperCase();
        tx.senderAccount=sender.accountNumber; tx.receiverAccount=receiverNumber; tx.receiverName=receiverName;
        tx.amount=amount; tx.description=description; tx.suspicious=suspicious;
        if (suspicious) {
            tx.status="PENDING_VERIFICATION";
            transactions.save(tx);
            notifications.save(new BankNotification(username,"Verification required",
                "A transfer of ₹"+String.format("%.2f",amount)+" was flagged by the demo fraud rules. No money has been moved. OTP delivery is simulated.", "SECURITY"));
            return transactionView(tx);
        }

        BankAccount receiver=accounts.findByAccountNumber(receiverNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver account number was not found in this demo"));
        if (!"ACTIVE".equals(receiver.status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Receiver account is not active");
        sender.balance-=amount; receiver.balance+=amount;
        accounts.save(sender); accounts.save(receiver);
        tx.status="COMPLETED"; transactions.save(tx);
        notifications.save(new BankNotification(sender.username,"Transfer successful","₹"+String.format("%.2f",amount)+" sent to "+receiver.holderName+". Ref: "+tx.reference,"TRANSACTION"));
        notifications.save(new BankNotification(receiver.username,"Money received","₹"+String.format("%.2f",amount)+" received from "+sender.holderName+". Ref: "+tx.reference,"TRANSACTION"));
        return transactionView(tx);
    }

    @GetMapping("/demo-accounts")
    public List<Map<String,Object>> demoAccounts() {
        return accounts.findAll().stream().map(a -> Map.<String,Object>of("name",a.holderName,"accountNumber",a.accountNumber)).toList();
    }

    private BankAccount getAccount(String username) {
        return accounts.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));
    }
    private Map<String,Object> accountView(BankAccount a) {
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("username",a.username); m.put("holderName",a.holderName); m.put("accountNumber",a.accountNumber);
        m.put("accountType",a.accountType); m.put("status",a.status); m.put("balance",a.balance); return m;
    }
    private Map<String,Object> transactionView(BankTransaction t) {
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("id",t.id); m.put("reference",t.reference); m.put("senderAccount",t.senderAccount);
        m.put("receiverAccount",t.receiverAccount); m.put("receiverName",t.receiverName); m.put("amount",t.amount);
        m.put("status",t.status); m.put("description",t.description); m.put("suspicious",t.suspicious); m.put("createdAt",t.createdAt);
        return m;
    }
}
