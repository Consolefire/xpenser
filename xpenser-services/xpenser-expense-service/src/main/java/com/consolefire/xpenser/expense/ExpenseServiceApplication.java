package com.consolefire.xpenser.expense;

import com.consolefire.xpenser.expense.config.ExpenseServiceDataConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({ExpenseServiceDataConfiguration.class})
@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.expense"})
public class ExpenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseServiceApplication.class, args);
    }
}
