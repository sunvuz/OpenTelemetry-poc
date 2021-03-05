package com.sample.awsplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication()
public class ConsumeMicroServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ConsumeMicroServiceApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ConsumeMicroServiceApplication.class, args);
	}

}
