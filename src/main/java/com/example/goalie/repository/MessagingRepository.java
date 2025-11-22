package com.example.goalie.repository;

import com.example.goalie.model.Message;
import com.example.goalie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessagingRepository extends JpaRepository<Message,Long> {
    List<Message> findBySenderOrReceiver(User sender, User receiver);
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);
}
