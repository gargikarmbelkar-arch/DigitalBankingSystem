package com.gargi.banking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoData implements CommandLineRunner {
    private final AccountRepository accounts;
    public DemoData(AccountRepository accounts) { this.accounts=accounts; }
    @Override public void run(String... args) {
        if (accounts.count()==0) {
            accounts.save(new BankAccount("1000000001","Gargi Karmbelkar","gargi","gargi123",75000));
            accounts.save(new BankAccount("1000000002","Bhumika Pawar","bhumika","demo123",25000));
            accounts.save(new BankAccount("1000000003","Pranav Chavan","pranav","demo123",40000));
        }
    }
}
