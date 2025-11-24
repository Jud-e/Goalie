package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String score;
    private String result;

    @ManyToOne
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @ManyToOne(optional = true)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
}
