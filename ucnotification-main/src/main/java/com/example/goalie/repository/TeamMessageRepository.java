package com.example.goalie.repository;

import com.example.goalie.model.Team;
import com.example.goalie.model.TeamMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMessageRepository extends JpaRepository<TeamMessage, Long> {

    List<TeamMessage> findByTeamOrderByTimestampAsc(Team team);
}
