package com.example.goalie.repository;

import com.example.goalie.config.PlayerTeamId;
import com.example.goalie.model.PlayerTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, PlayerTeamId> {
}
