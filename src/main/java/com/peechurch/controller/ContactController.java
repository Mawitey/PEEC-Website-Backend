package com.peechurch.controller;

import com.peechurch.model.ContactRequest;
import com.peechurch.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> submitContact(@RequestBody ContactRequest req) {


        String churchInbox = "mariamawitkebede8@gmail.com";

        emailService.sendContactEmail(
                churchInbox,
                req.getName(),
                req.getEmail(),
                req.getMessage()
        );

        return ResponseEntity.ok("Message sent");
    }
}
