package com.example.goalie.repository;

import com.example.goalie.config.PlayerTeamId;
import com.example.goalie.model.PlayerTeam;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import com.example.goalie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, PlayerTeamId> {
    // Find all teams a user is part of
    List<PlayerTeam> findByUser(User user);
    // Check if user is in a specific team
    Optional<PlayerTeam> findByUserAndTeam(User user, Team team);
    // Find if user is in any team for a tournament
    @Query("SELECT pt FROM PlayerTeam pt WHERE pt.user = :user AND pt.team.tournament = :tournament")
    List<PlayerTeam> findByUserAndTournament(@Param("user") User user, @Param("tournament") Tournament tournament);
    // Get all players in a team
    List<PlayerTeam> findByTeam(Team team);
}
