package com.example.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AssetsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetsManagerApplication.class, args);
	}

}
