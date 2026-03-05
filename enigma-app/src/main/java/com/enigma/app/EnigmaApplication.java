package com.enigma.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "patmal.course.enigma.entities")
@EnableJpaRepositories(basePackages = "patmal.course.enigma.repositories")
@SpringBootApplication(scanBasePackages = {"com.enigma.app","com.course", "patmal.course"})
public class EnigmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnigmaApplication.class, args);
    }
}

