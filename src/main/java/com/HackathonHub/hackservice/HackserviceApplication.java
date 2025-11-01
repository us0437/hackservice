package com.HackathonHub.hackservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackserviceApplication.class, args);
	}
}