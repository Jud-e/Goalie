package com.example.goalie.repository;

import com.example.goalie.model.Messaging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagingRepository extends JpaRepository<Messaging,Long> {
}
