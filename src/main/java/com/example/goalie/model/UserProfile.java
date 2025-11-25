package com.example.goalie.model;

import com.example.goalie.goalieEnum.AccountType;
import com.example.goalie.goalieEnum.DominantFoot;
import com.example.goalie.goalieEnum.Position;
import com.example.goalie.goalieEnum.SkillLevel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "userprofile")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String playerNickname;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Position preferredPosition;
    private Position secondaryPosition;
    private Integer skillRating; // 1-5 star rating
    private SkillLevel skillLevel;
    private DominantFoot dominantFoot;
    private String profilePicture;
    private AccountType accountType;
    private String bio;
    private String phoneNumber;

}
