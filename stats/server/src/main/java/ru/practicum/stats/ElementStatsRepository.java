package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.ElementStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElementStatsRepository extends JpaRepository<ElementStats, Long> {

    @Query("SELECT e FROM ElementStats e " +
            "WHERE e.createdDate BETWEEN :start AND :end " +
            "AND  e.uri IN :uris")
    List<ElementStats> getStatsNotOriginalIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT e FROM ElementStats e " +
            "WHERE e.createdDate BETWEEN :start AND :end")
    List<ElementStats> getStatsNotOriginalIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM ElementStats e " +
            "WHERE e.id IN (SELECT MIN(e2.id) FROM ElementStats e2 " +
            "WHERE e2.createdDate BETWEEN :start AND :end " +
            "GROUP BY e2.ip, e2.app) " +
            "AND e.uri IN :uris")
    List<ElementStats> getStatsOriginalIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT e FROM ElementStats e " +
            "WHERE e.id IN (SELECT MIN(e2.id) FROM ElementStats e2 " +
            "WHERE e2.createdDate BETWEEN :start AND :end " +
            "GROUP BY e2.ip, e2.app)")
    List<ElementStats> getStatsOriginalIp(LocalDateTime start, LocalDateTime end);

}
