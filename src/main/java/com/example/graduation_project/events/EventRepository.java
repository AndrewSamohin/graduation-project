package com.example.graduation_project.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventsEntity, Long> {

    @Query("""
        SELECT e FROM EventsEntity e
        WHERE (:name IS NULL OR e.name LIKE %:name%) 
        AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)   
        AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)    
        AND (CAST (:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter)
        AND (CAST (:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore)
        AND (:costMin IS NULL OR e.cost >= :costMin)
        AND (:costMax IS NULL OR e.cost <= :costMax)
        AND (:durationMin IS NULL OR e.duration >= :durationMin)
        AND (:durationMax IS NULL OR e.duration <= :durationMax)
        AND (:locationId IS NULL OR e.locationId = :locationId)
        AND (:eventStatus IS NULL OR e.status = :eventStatus)                                                                                
        """)
    List<EventsEntity> findEvents(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") BigDecimal costMin,
            @Param("costMax") BigDecimal costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Integer locationId,
            @Param("eventStatus") EventStatus eventStatus
            );

    @Modifying
    @Transactional
    @Query("""
        UPDATE EventsEntity e
        SET e.status = com.example.graduation_project.events.EventStatus.STARTED
        WHERE e.status = com.example.graduation_project.events.EventStatus.WAIT_START
        AND e.date <= :now
    """)
    int changeEventsAsStarted(LocalDateTime now);

    @Modifying
    @Transactional
    @Query("""
        UPDATE EventsEntity e
        SET e.status = com.example.graduation_project.events.EventStatus.FINISHED
        WHERE e.status = com.example.graduation_project.events.EventStatus.CANCELLED
        AND e.date <= :endTime
    """)
    int changeEventsAsFinished(@Param("endTime") LocalDateTime endTime);

    List<EventsEntity> findByStatus(EventStatus status);

    List<EventsEntity> findByOwnerId(Long currentUserId);
}
