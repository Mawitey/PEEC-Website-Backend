package com.peechurch;

import com.peechurch.model.Sermon;
import com.peechurch.repository.SermonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PeechurchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeechurchBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner init(SermonRepository repo) {
        return args -> {
            repo.deleteAll();
            repo.save(new Sermon("Jesus is Lord", "Pastor Nega", "2025-12-24"));
        };
    }

}

