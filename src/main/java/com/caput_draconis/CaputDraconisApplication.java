package com.caput_draconis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CaputDraconisApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaputDraconisApplication.class, args);
	}

}