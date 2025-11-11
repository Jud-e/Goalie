package com.example.goalie.team;

import java.util.List;

public class TeamTemplate {

    private String teamName;
    private String coachName;
    private String tournamentName;
    private List<PlayerInfo> players;
    private List<MatchInfo> matches;

    // Inner class for Player information
    public static class PlayerInfo {
        private String name;
        private String position;
        private String skillLevel;
        private int jerseyNumber; // Updated from matchesPlayed

        public PlayerInfo(String name, String position, String skillLevel, int jerseyNumber) {
            this.name = name;
            this.position = position;
            this.skillLevel = skillLevel;
            this.jerseyNumber = jerseyNumber;
        }

        // Getters
        public String getName() { return name; }
        public String getPosition() { return position; }
        public String getSkillLevel() { return skillLevel; }
        public int getJerseyNumber() { return jerseyNumber; }
    }

    // Inner class for Match information
    public static class MatchInfo {
        private String opponent;
        private String status;
        private String score;

        public MatchInfo(String opponent, String status, String score) {
            this.opponent = opponent;
            this.status = status;
            this.score = score;
        }

        // Getters
        public String getOpponent() { return opponent; }
        public String getStatus() { return status; }
        public String getScore() { return score; }
    }

    // Getters and Setters for TeamTemplate fields
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public List<PlayerInfo> getPlayers() { return players; }
    public void setPlayers(List<PlayerInfo> players) { this.players = players; }

    public List<MatchInfo> getMatches() { return matches; }
    public void setMatches(List<MatchInfo> matches) { this.matches = matches; }
}