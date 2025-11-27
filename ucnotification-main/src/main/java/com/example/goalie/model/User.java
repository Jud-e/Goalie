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
    private List<TeamMessage> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
