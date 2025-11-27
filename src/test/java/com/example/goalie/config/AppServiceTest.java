package com.example.goalie.config;

import com.example.goalie.model.Tournament;
import com.example.goalie.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AppServiceTest {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    void createTournament() {
        Tournament t = new Tournament();
        t.setName("Local Cup");
        t.setStartDate(LocalDate.now());
        t.setEndDate(LocalDate.now().plusDays(3));
        t.setLocation("Field 1");
        t.setNumOfTeams(4);
        t.setStatus(Tournament.TournamentStatus.UPCOMING);

        tournamentRepository.save(t);

        Tournament fetched = tournamentRepository.findById(t.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo("Local Cup");
    }
}