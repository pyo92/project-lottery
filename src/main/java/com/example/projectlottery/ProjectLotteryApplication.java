package com.example.projectlottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProjectLotteryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectLotteryApplication.class, args);
    }

}
