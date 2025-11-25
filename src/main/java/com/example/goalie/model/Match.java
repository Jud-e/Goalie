package com.example.goalie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String score;
//    private String result;

    @ManyToOne
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @ManyToOne(optional = true)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;


    private LocalDate matchDate;


    private Integer team1Score;
    private Integer team2Score;


    @Enumerated(EnumType.STRING)
    private MatchRound round;

    public String getFormattedMatchDate()
    {
        if (matchDate == null) return "";
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(matchDate);
    }

    public String getScoreDisplay()
    {
        if (team1Score == null || team2Score == null) return "";
        return team1Score + " - " + team2Score;
    }

    public enum MatchRound {
        ROUND_OF_16("Round of 16"),
        QUARTER_FINAL("Quarter Final"),
        SEMI_FINAL("Semi Final"),
        FINAL("Final"),
        THIRD_PLACE("Third Place");

        private final String displayName;

        MatchRound(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }


    }
    public String getTeamDisplay() {
        if (team1 == null && team2 == null) {
            return "TBD";
        }
        if (team1 == null) {
            return "TBD / " + team2.getName();
        }
        if (team2 == null) {
            return team1.getName() + " / TBD";
        }
        return team1.getName() + " / " + team2.getName();
    }

}