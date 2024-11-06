package com.syncit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.syncit")
public class SyncitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncitApplication.class, args);
	}

}
