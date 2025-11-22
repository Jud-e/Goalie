package com.example.goalie.model;

import com.example.goalie.config.PlayerTeamId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(PlayerTeamId.class)
public class PlayerTeam {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
