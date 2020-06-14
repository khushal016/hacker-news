package com.paytm.insider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.paytm.insider")
public class HackerNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackerNewsApplication.class, args);
	}

}
