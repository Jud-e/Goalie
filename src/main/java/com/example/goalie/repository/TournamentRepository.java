package com.example.goalie.repository;

import com.example.goalie.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
    List<Tournament> findByStatus(Tournament.TournamentStatus status);
    
    List<Tournament> findBySport(String sport);
    
    List<Tournament> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT t FROM Tournament t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.sport) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Tournament> searchTournaments(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM Tournament t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:sport IS NULL OR LOWER(t.sport) = LOWER(:sport)) AND " +
           "(:location IS NULL OR LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.endDate <= :endDate)")
    List<Tournament> filterTournaments(
        @Param("status") Tournament.TournamentStatus status,
        @Param("sport") String sport,
        @Param("location") String location,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Tournament t WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.sport) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:sport IS NULL OR LOWER(t.sport) = LOWER(:sport)) AND " +
           "(:location IS NULL OR LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.endDate <= :endDate)")
    List<Tournament> searchAndFilterTournaments(
        @Param("searchTerm") String searchTerm,
        @Param("status") Tournament.TournamentStatus status,
        @Param("sport") String sport,
        @Param("location") String location,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}

