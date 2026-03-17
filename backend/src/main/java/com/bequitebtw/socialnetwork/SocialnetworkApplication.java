package com.bequitebtw.socialnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SocialnetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialnetworkApplication.class, args);
    }

}
