package com.example.goalie.repository;

import com.example.goalie.model.Notification;
import com.example.goalie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver(User receiver);
}
