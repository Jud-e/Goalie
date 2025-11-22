package com.example.goalie.config;

import com.example.goalie.model.*;
import com.example.goalie.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class AppService {
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final NotificationRepository notificationRepository;
    private final MessagingRepository messagingRepository;
    private final MatchRepository matchRepository;
    private final UserProfileRepository userProfileRepository;

    public AppService(UserRepository userRepository,
                      TournamentRepository tournamentRepository,TeamRepository teamRepository,
                      PlayerTeamRepository playerTeamRepository,NotificationRepository notificationRepository,
                      MessagingRepository messagingRepository,
                      MatchRepository matchRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.playerTeamRepository = playerTeamRepository;
        this.notificationRepository = notificationRepository;
        this.messagingRepository = messagingRepository;
        this.matchRepository = matchRepository;
        this.userProfileRepository = userProfileRepository;
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

    // ================= Team =================
    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id){
        return teamRepository.findById(id).orElse(null);
    }

    public Team createTeam(Team team){
        return teamRepository.save(team);
    }
    public List<Team> getTeamsByTournament(Tournament tournament){
        return teamRepository.findByTournament(tournament);
    }

    // ================= Notifications =================
    public List<Notification> getNotificationsByUser(User user){
        return notificationRepository.findByReceiver(user);
    }

    public Notification createNotification(User receiver, String message){
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setDate(new java.util.Date());
        return notificationRepository.save(notification);
    }

    // ================= Messaging =================
    public List<Message> getMessagesForUser(User user){
        return messagingRepository.findBySenderOrReceiver(user, user);
    }

    public List<Message> getConversation(User user1, User user2){
        return messagingRepository.findConversation(user1, user2);
    }

    public Message sendMessage(User sender, User receiver, String content){
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return messagingRepository.save(message);
    }

    // ================= Match =================
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id){
        return matchRepository.findById(id).orElse(null);
    }

    public Match createMatch(Match match){
        return matchRepository.save(match);
    }


    // ================== User Profile ===============

}
