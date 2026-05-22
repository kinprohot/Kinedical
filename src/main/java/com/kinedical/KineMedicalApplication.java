package com.kinedical;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.kinedical.service.UserService;

@SpringBootApplication
public class KineMedicalApplication {

    public static void main(String[] args) {
        SpringApplication.run(KineMedicalApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultUsers(UserService userService) {
        return args -> userService.initializeDefaultAccounts();
    }
}
