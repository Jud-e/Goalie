package com.example.goalie.repository;

import com.example.goalie.config.PlayerTeamId;
import com.example.goalie.model.PlayerTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.goalie.model.Team;
import java.util.List;

public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, PlayerTeamId> {
    // Custom method to find all players for a given team
    List<PlayerTeam> findByTeam(Team team);
}
