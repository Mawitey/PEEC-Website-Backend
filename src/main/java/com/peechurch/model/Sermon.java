package com.peechurch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sermon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String speaker;
    private String date;

    // Constructors
    public Sermon() {}
    public Sermon(String title, String speaker, String date) {
        this.title = title;
        this.speaker = speaker;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
