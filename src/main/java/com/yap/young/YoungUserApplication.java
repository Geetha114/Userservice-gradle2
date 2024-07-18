package com.yap.young;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YoungUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoungUserApplication.class, args);
	}

}
