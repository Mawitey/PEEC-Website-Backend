package com.peechurch.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/sermons")
@CrossOrigin(origins = "*") // IMPORTANT
public class SermonController {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/youtube")
    public ResponseEntity<String> getYoutubeSermons() {

        String url =
                "https://www.googleapis.com/youtube/v3/search" +
                        "?part=snippet" +
                        "&channelId=" + channelId +
                        "&maxResults=10" +
                        "&order=date" +
                        "&type=video" +
                        "&key=" + apiKey;

        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }

}

