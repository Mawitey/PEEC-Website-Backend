package com.peechurch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Sermon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String preacher;

    private LocalDate sermonDate;

    private String youtubeId;

    // 🔹 NEW
    private String youtubeUrl;

    // 🔹 Optional (used for iframe embedding)
    private String youtubeVideoId;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreacher() {
        return preacher;
    }

    public void setPreacher(String preacher) {
        this.preacher = preacher;
    }

    public LocalDate getSermonDate() {
        return sermonDate;
    }

    public void setSermonDate(LocalDate sermonDate) {
        this.sermonDate = sermonDate;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }
}
