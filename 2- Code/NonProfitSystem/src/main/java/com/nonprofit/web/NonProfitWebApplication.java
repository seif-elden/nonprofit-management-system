package com.nonprofit.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Browser entry point. The original com.nonprofit.Main still runs the console. */
@SpringBootApplication
public class NonProfitWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(NonProfitWebApplication.class, args);
    }
}
