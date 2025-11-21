package com.example.goalie.repository;

import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTournament(Tournament tournament);
}
