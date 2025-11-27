package com.example.goalie.config;

import com.example.goalie.goalieEnum.DominantFoot;
import com.example.goalie.goalieEnum.Position;
import com.example.goalie.goalieEnum.SkillLevel;
import com.example.goalie.model.*;
import com.example.goalie.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AppService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final NotificationRepository notificationRepository;
    private final TeamMessageRepository teamMessageRepository;
    private final MatchRepository matchRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordResetTokenRepository tokenRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public AppService(UserRepository userRepository,
                      TournamentRepository tournamentRepository,
                      TeamRepository teamRepository,
                      PlayerTeamRepository playerTeamRepository,
                      NotificationRepository notificationRepository, TeamMessageRepository teamMessageRepository,
                      MatchRepository matchRepository,
                      UserProfileRepository userProfileRepository,
                      PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.playerTeamRepository = playerTeamRepository;
        this.notificationRepository = notificationRepository;
        this.teamMessageRepository = teamMessageRepository;
        this.matchRepository = matchRepository;
        this.userProfileRepository = userProfileRepository;
        this.tokenRepository = passwordResetTokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // hashed password
                .roles("USER") // you can map roles from User if you have them
                .build();
    }
    //For users
    public User createUser(User user){
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Option 1: throw an exception
            throw new IllegalArgumentException("A user with this email already exists");
        }
        user.setSubscription(false);
        return userRepository.save(user);
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
    public boolean isUserInAnyTeam(User user, Tournament tournament) {
        if (user == null || tournament == null) {
            return false;
        }
        List<Team> teams = getTeamsByTournament(tournament);
        if (teams == null) {
            return false;
        }
        for (Team team : teams) {
            if (team.getPlayers() != null) {
                for (User player : team.getPlayers()) {
                    if (player != null && player.getId().equals(user.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public String createPasswordResetToken(User user) {
        tokenRepository.deleteByUserId(user.getId());

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(token);
        return token.getToken();
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken prt = tokenRepository.findByToken(token).orElse(null);
        return prt != null && prt.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken prt = tokenRepository.findByToken(token).orElse(null);
        return prt != null ? prt.getUser() : null;
    }

    public void updatePassword(User user, String newRawPassword) {
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        tokenRepository.deleteByUserId(user.getId());
    }


    //For Tournaments
    public List<Tournament> getAllTournaments(){
        return tournamentRepository.findAll();
    }


    public Tournament getTournamentById(Long id){
        return tournamentRepository.findById(id).orElse(null);
    }

    public Tournament createTournament(Tournament tournament, User organizer){
        return tournamentRepository.save(tournament);
    }

    public List<Tournament> searchTournaments(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTournaments();
        }
        return tournamentRepository.searchTournaments(searchTerm.trim());
    }

    public List<Tournament> filterTournaments(
            String status,
            String location,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Tournament.TournamentStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Tournament.TournamentStatus.fromString(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        String locationFilter = (location != null && !location.trim().isEmpty()) ? location.trim() : null;

        return tournamentRepository.filterTournaments(
                statusEnum,
                locationFilter,
                startDate,
                endDate
        );
    }
    public List<Tournament> searchAndFilterTournaments(
            String searchTerm,
            String status,
            String location,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Tournament.TournamentStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Tournament.TournamentStatus.fromString(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }

        String searchFilter = (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null;
        String locationFilter = (location != null && !location.trim().isEmpty()) ? location.trim() : null;

        return tournamentRepository.searchAndFilterTournaments(
                searchFilter,
                statusEnum,
                locationFilter,
                startDate,
                endDate
        );
    }

    public void updateTournamentStatus(Tournament tournament) {
        LocalDate today = LocalDate.now();

        if (tournament.getEndDate() != null && tournament.getEndDate().isBefore(today)) {
            // End date has passed → tournament is completed
            tournament.setStatus(Tournament.TournamentStatus.COMPLETED);
        } else if (tournament.getStartDate() != null && tournament.getStartDate().isAfter(today)) {
            // Start date is in the future → upcoming
            tournament.setStatus(Tournament.TournamentStatus.UPCOMING);
        } else if (tournament.getStartDate() != null &&
                !tournament.getStartDate().isAfter(today) &&
                (tournament.getEndDate() == null || !tournament.getEndDate().isBefore(today))) {
            // Tournament is ongoing → active
            tournament.setStatus(Tournament.TournamentStatus.ACTIVE);
        }
        // Save changes
        tournamentRepository.save(tournament);
    }

    public Tournament saveTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
    public List<String> getAllLocations() {
        return tournamentRepository.findAll().stream()
                .map(Tournament::getLocation)
                .filter(loc -> loc != null && !loc.trim().isEmpty())
                .distinct()
                .sorted()
                .toList();
    }
    public void deleteTournament(Tournament tournament) {
        List<Team> teams = teamRepository.findByTournament(tournament);
        for (Team team : teams) {
            playerTeamRepository.deleteAll(
                    playerTeamRepository.findByTeam(team)
            );
        }
        List<Match> matches = matchRepository.findByTournament(tournament);
        if (!matches.isEmpty()) {
            matchRepository.deleteAll(matches);
        }
        if (!teams.isEmpty()) {
            teamRepository.deleteAll(teams);
        }
        tournamentRepository.delete(tournament);
    }


    // ================= Team =================
    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id){
        Team team = teamRepository.findById(id).orElse(null);
        if (team != null) {
            loadTeamPlayers(team);
        }        return team;
    }
    @Transactional
    public Team createTeam(Team team){
        // Ensure players list is initialized
        if (team.getPlayers() == null) {
            team.setPlayers(new java.util.ArrayList<>());
        }
        return teamRepository.save(team);
    }
    public List<Team> getTeamsByTournament(Tournament tournament){
        List<Team> teams = teamRepository.findByTournament(tournament);
        // Load players for each team from PlayerTeam
        for (Team team : teams) {
            loadTeamPlayers(team);
        }
        return teams;
    }
    // Load players for a team from PlayerTeam entity
    private void loadTeamPlayers(Team team) {
        if (team == null) return;
        List<PlayerTeam> playerTeams = playerTeamRepository.findByTeam(team);
        if (team.getPlayers() == null) {
            team.setPlayers(new java.util.ArrayList<>());
        } else {
            team.getPlayers().clear();
        }
        for (PlayerTeam pt : playerTeams) {
            if (pt.getUser() != null && !team.getPlayers().contains(pt.getUser())) {
                team.getPlayers().add(pt.getUser());
            }
        }
    }
    // Check if user is premium (subscription = true means premium)
    public boolean isPremiumUser(User user) {
        return user != null && user.isSubscription();
    }
    // Check if user is already in a team for a tournament
    public boolean isUserInTeamForTournament(User user, Tournament tournament) {
        if (user == null || tournament == null) return false;
        List<PlayerTeam> playerTeams = playerTeamRepository.findByUserAndTournament(user, tournament);
        return !playerTeams.isEmpty();
    }
    // Get the team user is in for a tournament
    public Team getUserTeamForTournament(User user, Tournament tournament) {
        List<PlayerTeam> playerTeams = playerTeamRepository.findByUserAndTournament(user, tournament);
        if (playerTeams.isEmpty()) return null;
        return playerTeams.get(0).getTeam();
    }
    // Join a specific team (for premium users)
    @Transactional
    public boolean joinTeam(User user, Team team) {
        if (user == null || team == null) return false;
// Check if user is already in this team
        if (playerTeamRepository.findByUserAndTeam(user, team).isPresent()) {
            return false; // Already in team
        }
        // Check if user is already in another team for the same tournament
        if (team.getTournament() != null && isUserInTeamForTournament(user, team.getTournament())) {
            return false; // Already in a team for this tournament
        }
        // Create PlayerTeam relationship
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setUser(user);
        playerTeam.setTeam(team);
        playerTeamRepository.save(playerTeam);
        // Also add to Team's players list if using ManyToMany
        if (team.getPlayers() == null) {
            team.setPlayers(new java.util.ArrayList<>());
        }
        if (!team.getPlayers().contains(user)) {
            team.getPlayers().add(user);
            teamRepository.save(team);
        }
        return true;    }
    // Join a random team (for regular users)
    public boolean joinRandomTeam(User user, Tournament tournament) {
        if (user == null || tournament == null) return false;
        // Check if user is already in a team for this tournament
        if (isUserInTeamForTournament(user, tournament)) {
            return false; // Already in a team
        }
        // Get all teams for the tournament
        List<Team> teams = getTeamsByTournament(tournament);
        if (teams.isEmpty()) {
            return false; // No teams available
        }
        // Filter teams that user is not already in and shuffle
        java.util.Collections.shuffle(teams);
        // Try to join the first available team
        for (Team team : teams) {
            if (joinTeam(user, team)) {
                return true; // Successfully joined
            }
        }
        return false; // Could not join any team
    }


    public PlayerTeam addUserToTeam(PlayerTeam playerTeam) {
        return playerTeamRepository.save(playerTeam);
    }

    // ================= Notifications =================
    public List<Notification> getNotificationsByUser(User user){
        return notificationRepository.findByReceiver(user);
    }

    public Notification getNotificationById(Long id){
        return notificationRepository.findById(id).orElse(null);
    }

    public Notification createNotification(User receiver, String message){
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setDate(new java.util.Date());
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id){
        notificationRepository.deleteById(id);
    }

    public void deleteAllNotificationsForUser(User user){
        List<Notification> notifications = notificationRepository.findByReceiver(user);
        notificationRepository.deleteAll(notifications);
    }

    // ================= Messaging =================

    public void sendMessage(Team team, User sender, String content) {
        TeamMessage msg = new TeamMessage();
        msg.setTeam(team);
        msg.setSender(sender);
        msg.setContent(content);
        teamMessageRepository.save(msg);
    }

    public List<TeamMessage> getTeamMessages(Team team) {
        return teamMessageRepository.findByTeamOrderByTimestampAsc(team);
    }

    // ================= Match =================
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id){
        return matchRepository.findById(id).orElse(null);
    }

    @Transactional
    public Match updateMatch(Match match){
        return matchRepository.save(match);
    }

    // ------------ Random Match Making --------

    public void generateRandomMatches(Long id){
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Tournament not found."));

        List<Team> teams = teamRepository.findByTournament(tournament);

        Collections.shuffle(teams);

        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < teams.size(); i+=2) {
            if(i + 1 < teams.size()) {
                Match match = createMatch(tournament, teams.get(i), teams.get(i+1));
                matches.add(match);
            }
        }

        matchRepository.saveAll(matches);
    }

    public List<Match> getMatchesByTournament(Tournament tournament){

        List<Match> matches = matchRepository.findMatchByTournament(tournament);

        if (matches.isEmpty()) {
            List<Team> teams = teamRepository.findByTournament(tournament);
            int numberOfTeams = teams.size();

            Set<Integer> teamSizes = Set.of(4,8,16,32);

            if (teamSizes.contains(numberOfTeams)) {
                generateRandomMatches(tournament.getId());
                matches = matchRepository.findMatchByTournament(tournament);
            }
        }
        return matches;
    }


    private Match createMatch(Tournament tournament, Team team1, Team team2) {
        Match match = new Match();
        match.setTournament(tournament);
        match.setTeam1(team1);
        match.setTeam2(team2);
        if (tournament.getStartDate() != null) {
            match.setMatchDate(tournament.getStartDate().plusDays(new Random().nextInt(7)));
        } else {
            match.setMatchDate(LocalDate.now());
        }

        return match;
    }


    public  void deleteMatch(Long id){
        matchRepository.deleteById(id);
    }
    public void deleteAllMatches(Long id){
        matchRepository.deleteMatchesByTournamentId(id);
    }



    // ================== User Profile ===============
    public boolean hasPlayerProfile(User user) {
        return userProfileRepository.findAll().stream()
                .anyMatch(profile -> profile.getUser() != null && profile.getUser().getId().equals(user.getId()));
    }

    public UserProfile getPlayerProfile(User user) {
        return userProfileRepository.findAll().stream()
                .filter(profile -> profile.getUser() != null && profile.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
    }

    public UserProfile createOrUpdatePlayerProfile(User user, String playerNickname, Integer skillRating,
                                                   String preferredPosition, String dominantFoot, String bio) {
        UserProfile profile = getPlayerProfile(user);

        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setPlayerNickname(playerNickname);
        profile.setSkillRating(skillRating);

        // Map skill rating to SkillLevel enum
        if (skillRating != null) {
            if (skillRating <= 2) {
                profile.setSkillLevel(SkillLevel.BEGINNER);
            } else if (skillRating <= 4) {
                profile.setSkillLevel(SkillLevel.INTERMEDIATE);
            } else {
                profile.setSkillLevel(SkillLevel.ADVANCED);
            }
        }

        if (preferredPosition != null && !preferredPosition.isEmpty()) {
            try {
                profile.setPreferredPosition(Position.valueOf(preferredPosition.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid position, ignore
            }
        }

        if (dominantFoot != null && !dominantFoot.isEmpty()) {
            try {
                profile.setDominantFoot(DominantFoot.valueOf(dominantFoot.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid foot, ignore
            }
        }

        if (bio != null) {
            profile.setBio(bio);
        }

        return userProfileRepository.save(profile);
    }

}