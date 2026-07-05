package com.telecom.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class TelecomScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelecomScmApplication.class, args);
    }
}
