package com.peechurch;

import com.peechurch.model.Sermon;
import com.peechurch.repository.SermonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class PeechurchBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeechurchBackendApplication.class, args);
    }
//        public static void main(String[] args) {
//            SpringApplication app = new SpringApplication(PeechurchBackendApplication.class);
//            app.setDefaultProperties(Collections.singletonMap("spring.datasource.url", "jdbc:h2:mem:testdb")); // in-memory DB
//            app.run(args);
//        }








//This was for test
//    @Bean
//    CommandLineRunner init(SermonRepository repo) {
//        return args -> {
//            repo.deleteAll();
//            repo.save(new Sermon("Jesus is Lord", "Pastor Nega", "2025-12-24"));
//        };
//    }

}

