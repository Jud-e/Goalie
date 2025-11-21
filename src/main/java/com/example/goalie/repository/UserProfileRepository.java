package com.example.goalie.repository;

import com.example.goalie.model.User;
import com.example.goalie.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository  extends JpaRepository<UserProfile,Integer> {

}
