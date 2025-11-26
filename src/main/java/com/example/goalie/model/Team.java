//package com.example.goalie.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Data
//public class Team {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//
//    @ManyToOne(optional = true)
//    @JoinColumn(name = "tournament_id")
//    private Tournament tournament;
//
//    @ManyToMany(mappedBy = "teams")
//    private List<User> players = new ArrayList<>();
//
//}

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

    @ManyToOne(optional = true)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    // 🚨 1. ADD THIS FIELD 🚨
    // This establishes the link from Team to the intermediate PlayerTeam entity.
    // It enables the getPlayerTeams() method via @Data.
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerTeam> playerTeams = new ArrayList<>();

    // 🚨 2. IMPORTANT NOTE 🚨
    // This is the redundant relationship causing the original DataIntegrityViolationException.
    // It creates the problematic 'TEAM_USERS' join table.
    @ManyToMany(mappedBy = "teams")
    private List<User> players = new ArrayList<>();
}
