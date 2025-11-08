package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role;
    private String skill;
    private boolean subscription;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "receiver")
    private List<Notification> notification = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Messaging> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PlayerTeam> playerTeams;
}
