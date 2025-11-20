package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String location;

    @Column(nullable = false)
    private String sport;

    private Integer maxParticipants;

    private Integer currentParticipants;

    public enum TournamentStatus {
        UPCOMING, ACTIVE, COMPLETED, CANCELLED
    }
}

