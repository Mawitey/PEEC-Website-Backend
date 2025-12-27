package com.peechurch.controller;

import com.peechurch.model.Sermon;
import com.peechurch.repository.SermonRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Allow React dev server
public class SermonController {

    private final SermonRepository sermonRepository;

    public SermonController(SermonRepository sermonRepository) {
        this.sermonRepository = sermonRepository;
    }

    @GetMapping("/api/sermons")
    public List<Sermon> getAllSermons() {
        return sermonRepository.findAll();
    }
}
