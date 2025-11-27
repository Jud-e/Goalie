package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class TeamMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User sender;

    @Column(nullable = false)
    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();
}
