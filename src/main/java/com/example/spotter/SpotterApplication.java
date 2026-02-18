package com.example.spotter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpotterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotterApplication.class, args);
	}

}
