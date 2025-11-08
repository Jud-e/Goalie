package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
//
//    @OneToMany(mappedBy = "team")
//    private List<PlayerTeam> playerTeams;
//
//    @OneToMany(mappedBy = "match")
//    private List<Match> matches;
}
