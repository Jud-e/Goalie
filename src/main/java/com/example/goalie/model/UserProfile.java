package com.example.goalie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

enum Position {
    GOALKEEPER, DEFENDER, MIDFIELDER, FORWARD

}
enum SkillLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum DominantFoot {
    LEFT, RIGHT, BOTH
}

enum AccountType {
    REGULAR, PREMIUM
}

@Entity
@Data
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
