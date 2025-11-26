package com.example.goalie.repository;

import com.example.goalie.model.Match;
import com.example.goalie.model.Team;
import com.example.goalie.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

//    List<Match> findTournamentById(Long id);
    List<Match> findMatchByTournament(Tournament tournament);
    List<Match> findByTournament(Tournament tournament);



    @Query("SELECT m FROM Match m WHERE  m.matchDate > :now ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatches(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.tournament.id = :tournamentId " +
            "AND m.matchDate > :now ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatchesByTournament(
            @Param("tournamentId") Long tournamentId,
            @Param("now") LocalDateTime now);


    void deleteMatchesByTournamentId(Long tournamentId);

}