package com.example.goalie.model;

import jakarta.persistence.*;

@Entity
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String status; // e.g., Active / Inactive

    private int clicks;

    // Constructors
    public Advertisement() {}

    public Advertisement(String title, String status, int clicks) {
        this.title = title;
        this.status = status;
        this.clicks = clicks;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getClicks() { return clicks; }
    public void setClicks(int clicks) { this.clicks = clicks; }
}
