package com.example.goalie.config;

import com.example.goalie.model.User;
import com.example.goalie.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final NotificationRepository notificationRepository;
    private final MessagingRepository messagingRepository;
    private final MatchRepository matchRepository;

    public AppService(UserRepository userRepository,
                      TournamentRepository tournamentRepository,TeamRepository teamRepository,
                      PlayerTeamRepository playerTeamRepository,NotificationRepository notificationRepository,
                      MessagingRepository messagingRepository,
                      MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.playerTeamRepository = playerTeamRepository;
        this.notificationRepository = notificationRepository;
        this.messagingRepository = messagingRepository;
        this.matchRepository = matchRepository;
    }

    public User createUser(User user){
        user.setSubscription(false);
        return userRepository.save(user);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User not found"));
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }
}
