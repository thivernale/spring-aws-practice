package org.thivernale.springawspractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringAwsPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAwsPracticeApplication.class, args);
    }

}
