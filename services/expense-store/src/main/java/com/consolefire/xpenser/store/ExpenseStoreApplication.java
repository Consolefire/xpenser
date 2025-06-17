package com.consolefire.xpenser.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.store"})
@EntityScan(basePackages = {"com.consolefire.xpenser.store.data.entity"})
@EnableJpaRepositories(basePackages = {"com.consolefire.xpenser.store.data.repository"})
public class ExpenseStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseStoreApplication.class, args);
	}

}
