package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude =  {"matches", "teams"})
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    private Integer numOfTeams;

    private String location;

    public enum TournamentStatus {
        UPCOMING, ACTIVE, COMPLETED, CANCELLED;
        public static TournamentStatus fromString(String status) {
            if (status == null) return null;
            for (TournamentStatus ts : TournamentStatus.values()) {
                if (ts.name().equalsIgnoreCase(status.trim())) return ts;
            }
            return null; // or throw exception if you want strict handling
        }
    }
    @Transient // tells JPA not to store this in the DB
    public Integer getMaxParticipants() {
        return numOfTeams != null ? numOfTeams * 11 : 0;
    }
    @Transient
    public int getCurrentParticipants() {
        return participants != null ? participants.size() : 0;
    }

    @OneToMany(mappedBy = "tournament",cascade = CascadeType.ALL)
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();
}
