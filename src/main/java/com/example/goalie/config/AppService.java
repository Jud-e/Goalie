package com.example.goalie.config;

import com.example.goalie.model.*;
import com.example.goalie.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
        user.setSubscription(false);
        return userRepository.save(user);
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
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
        tournament.setStatus("Upcoming");
        return tournamentRepository.save(tournament);
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
