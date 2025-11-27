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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerTeam> playerTeams = new ArrayList<>();

    @ManyToMany(mappedBy = "teams")
    private List<User> players = new ArrayList<>();
}
