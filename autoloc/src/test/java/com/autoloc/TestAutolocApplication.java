package com.autoloc;

import org.springframework.boot.SpringApplication;

public class TestAutolocApplication {

	public static void main(String[] args) {
		SpringApplication.from(AutolocApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
