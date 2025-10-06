package com.example.PipReviewSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class PipReviewSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PipReviewSystemApplication.class, args);
	}

}
