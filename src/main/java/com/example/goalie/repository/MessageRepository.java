package com.example.goalie.repository;

import com.example.goalie.model.Message;
import com.example.goalie.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByTeamOrderByTimestampAsc(Team team);
}
