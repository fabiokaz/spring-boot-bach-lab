package com.kaz.spring_boot_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBootBatchLabApplication {

	// Common initialization
	/*public static void main(String[] args) {
		SpringApplication.run(SpringBootBatchLabApplication.class, args);
	}*/

	// explicitly manage the lifecycle using context.close to finish the application
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootBatchLabApplication.class, args);
		context.close();
	}

}
