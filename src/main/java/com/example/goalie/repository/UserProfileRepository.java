package com.example.goalie.repository;

import com.example.goalie.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository  extends JpaRepository<UserProfile,Integer> {
}
