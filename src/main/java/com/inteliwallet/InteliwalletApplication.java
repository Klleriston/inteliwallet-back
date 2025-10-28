package com.inteliwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InteliwalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteliwalletApplication.class, args);
    }
}