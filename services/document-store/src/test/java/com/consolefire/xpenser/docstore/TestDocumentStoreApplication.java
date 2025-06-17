package com.consolefire.xpenser.docstore;

import org.springframework.boot.SpringApplication;

public class TestDocumentStoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(DocumentStoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
