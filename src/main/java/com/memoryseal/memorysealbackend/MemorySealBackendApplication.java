package com.memoryseal.memorysealbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MemorySealBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemorySealBackendApplication.class, args);
	}

}
