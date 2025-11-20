package com.example.goalie.config;

import com.example.goalie.model.Tournament;
import com.example.goalie.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final TournamentRepository tournamentRepository;
    
    @Override
    public void run(String... args) {
        // Only initialize if database is empty
        if (tournamentRepository.count() == 0) {
            initializeSampleData();
        }
    }
    
    private void initializeSampleData() {
        // Sample tournaments for testing
        tournamentRepository.save(new Tournament(
            null,
            "Spring Soccer Championship",
            "Annual spring soccer tournament featuring teams from across the region. Competitive matches with exciting gameplay.",
            Tournament.TournamentStatus.ACTIVE,
            LocalDate.now().minusDays(5),
            LocalDate.now().plusDays(15),
            "City Sports Complex",
            "Soccer",
            16,
            12
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Summer Basketball League",
            "Join us for an intense summer basketball competition. Open to all skill levels.",
            Tournament.TournamentStatus.UPCOMING,
            LocalDate.now().plusDays(30),
            LocalDate.now().plusDays(60),
            "Downtown Arena",
            "Basketball",
            8,
            0
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Winter Hockey Tournament",
            "Fast-paced ice hockey tournament in the heart of winter. Experience the thrill of competitive hockey.",
            Tournament.TournamentStatus.UPCOMING,
            LocalDate.now().plusDays(45),
            LocalDate.now().plusDays(75),
            "Ice Rink Center",
            "Hockey",
            12,
            3
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Fall Tennis Open",
            "Professional-grade tennis tournament with cash prizes. Registration open to all players.",
            Tournament.TournamentStatus.COMPLETED,
            LocalDate.now().minusDays(60),
            LocalDate.now().minusDays(30),
            "Tennis Club",
            "Tennis",
            32,
            32
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Regional Volleyball Championship",
            "Regional volleyball championship bringing together the best teams from neighboring cities.",
            Tournament.TournamentStatus.ACTIVE,
            LocalDate.now().minusDays(10),
            LocalDate.now().plusDays(20),
            "Community Center",
            "Volleyball",
            20,
            18
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Youth Soccer Development",
            "Development tournament for young soccer players. Focus on skill building and sportsmanship.",
            Tournament.TournamentStatus.UPCOMING,
            LocalDate.now().plusDays(20),
            LocalDate.now().plusDays(40),
            "Youth Sports Field",
            "Soccer",
            24,
            15
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Beach Volleyball Classic",
            "Fun-filled beach volleyball tournament at the waterfront. Perfect for summer fun!",
            Tournament.TournamentStatus.CANCELLED,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(25),
            "Beachfront Courts",
            "Volleyball",
            16,
            0
        ));
        
        tournamentRepository.save(new Tournament(
            null,
            "Elite Basketball Showcase",
            "High-level basketball tournament featuring elite players and teams. Spectator-friendly event.",
            Tournament.TournamentStatus.ACTIVE,
            LocalDate.now().minusDays(3),
            LocalDate.now().plusDays(17),
            "Main Stadium",
            "Basketball",
            12,
            10
        ));
    }
}

