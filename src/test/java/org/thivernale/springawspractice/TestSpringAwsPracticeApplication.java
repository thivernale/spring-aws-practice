package org.thivernale.springawspractice;

import org.springframework.boot.SpringApplication;

public class TestSpringAwsPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringAwsPracticeApplication::main)
            .with(TestcontainersConfiguration.class)
            .run(args);
    }

}
