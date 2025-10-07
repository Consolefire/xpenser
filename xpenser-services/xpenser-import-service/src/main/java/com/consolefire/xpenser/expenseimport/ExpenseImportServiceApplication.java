package com.consolefire.xpenser.expenseimport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.expenseimport"})
public class ExpenseImportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseImportServiceApplication.class, args);
    }

}
