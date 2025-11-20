package com.example.goalie.service;

import com.example.goalie.model.Tournament;
import com.example.goalie.repository.TournamentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TournamentService {
    
    private final TournamentRepository tournamentRepository;
    
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }
    
    public List<Tournament> searchTournaments(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTournaments();
        }
        return tournamentRepository.searchTournaments(searchTerm.trim());
    }
    
    public List<Tournament> filterTournaments(
            String status,
            String sport,
            String location,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Tournament.TournamentStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Tournament.TournamentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        String sportFilter = (sport != null && !sport.trim().isEmpty()) ? sport.trim() : null;
        String locationFilter = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        
        return tournamentRepository.filterTournaments(
            statusEnum,
            sportFilter,
            locationFilter,
            startDate,
            endDate
        );
    }
    
    public List<Tournament> searchAndFilterTournaments(
            String searchTerm,
            String status,
            String sport,
            String location,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Tournament.TournamentStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Tournament.TournamentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        String searchFilter = (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null;
        String sportFilter = (sport != null && !sport.trim().isEmpty()) ? sport.trim() : null;
        String locationFilter = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        
        return tournamentRepository.searchAndFilterTournaments(
            searchFilter,
            statusEnum,
            sportFilter,
            locationFilter,
            startDate,
            endDate
        );
    }
    
    public Tournament saveTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
    
    public List<String> getAllSports() {
        return tournamentRepository.findAll().stream()
            .map(Tournament::getSport)
            .distinct()
            .sorted()
            .toList();
    }
    
    public List<String> getAllLocations() {
        return tournamentRepository.findAll().stream()
            .map(Tournament::getLocation)
            .filter(loc -> loc != null && !loc.trim().isEmpty())
            .distinct()
            .sorted()
            .toList();
    }
}

