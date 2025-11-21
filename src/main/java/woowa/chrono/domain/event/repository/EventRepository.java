package woowa.chrono.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.study.repository.StudyRecordProjection;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventLocation(String eventLocation);

    @Query("""
                SELECT sr.member AS member, SUM(sr.sessionTime) AS totalTimeSeconds
                FROM EventRecord er
                JOIN er.member m
                JOIN StudyRecord sr ON sr.member = m
                WHERE er.event.id = :eventId
                  AND sr.recordTime BETWEEN :startTime AND :endTime
                GROUP BY sr.member
            """)
    List<StudyRecordProjection> findStudySummaryByEvent(
            @Param("eventId") Long eventId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
