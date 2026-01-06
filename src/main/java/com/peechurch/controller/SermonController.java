package com.peechurch.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/sermons")
@CrossOrigin(origins = "*")
public class SermonController {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate = new RestTemplate();

    private static volatile String cachedJson = null;
    private static volatile Instant cacheTime = Instant.EPOCH;

    // 15 min cache
    private static final long CACHE_SECONDS = 15 * 60;

    @GetMapping("/youtube")
    public ResponseEntity<String> getSermonVideos() {

        // Serve fresh cache if available
        if (cachedJson != null && Instant.now().minusSeconds(CACHE_SECONDS).isBefore(cacheTime)) {
            return ResponseEntity.ok(cachedJson);
        }

        try {
            // 1) Get uploads playlist id
            String channelsUrl =
                    "https://www.googleapis.com/youtube/v3/channels" +
                            "?part=contentDetails" +
                            "&id=" + channelId +
                            "&key=" + apiKey;

            String channelsJson = restTemplate.getForObject(channelsUrl, String.class);
            String uploadsPlaylistId = extractUploadsPlaylistId(channelsJson);

            if (uploadsPlaylistId == null) {
                // If parsing fails, fall back to cached if any
                if (cachedJson != null) return ResponseEntity.ok(cachedJson);
                return ResponseEntity.status(503)
                        .body("{\"items\":[],\"error\":\"Unable to read uploads playlist.\"}");
            }

            // 2) Get latest videos from uploads playlist
            String playlistUrl =
                    "https://www.googleapis.com/youtube/v3/playlistItems" +
                            "?part=snippet,contentDetails" +
                            "&maxResults=12" +
                            "&playlistId=" + uploadsPlaylistId +
                            "&key=" + apiKey;

            String playlistJson = restTemplate.getForObject(playlistUrl, String.class);

            // Cache and return
            cachedJson = playlistJson;
            cacheTime = Instant.now();

            return ResponseEntity.ok(playlistJson);

        } catch (HttpClientErrorException e) {
            // quota / forbidden / etc. -> serve old cache if available
            if (cachedJson != null) return ResponseEntity.ok(cachedJson);

            return ResponseEntity.status(503)
                    .body("{\"items\":[],\"error\":\"YouTube temporarily unavailable (quota).\"}");

        } catch (Exception e) {
            if (cachedJson != null) return ResponseEntity.ok(cachedJson);
            return ResponseEntity.status(503)
                    .body("{\"items\":[],\"error\":\"Sermons temporarily unavailable.\"}");
        }
    }

    // Extracts uploads playlistId from the channels API JSON
    // This keeps you dependency-free (no JSON library needed).
    private String extractUploadsPlaylistId(String channelsJson) {
        // Looks for: "uploads": "UUxxxx"
        Pattern p = Pattern.compile("\"uploads\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(channelsJson);
        if (m.find()) return m.group(1);
        return null;
    }
}

