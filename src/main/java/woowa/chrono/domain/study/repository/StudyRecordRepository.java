package woowa.chrono.domain.study.repository;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.study.StudyRecord;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    @Query("SELECT sr.member AS member, SUM(sr.sessionTime) AS totalTimeSeconds "
            + "FROM StudyRecord sr "
            + "WHERE sr.recordTime BETWEEN :startTime AND :endTime "
            + "GROUP BY sr.member")
    StudyRecordProjection findTotalUsageTime(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    @Query("SELECT sr.member AS member, SUM(sr.sessionTime) AS totalTimeSeconds " +
            "FROM StudyRecord sr " +
            "WHERE sr.member = :member AND sr.recordTime BETWEEN :startTime AND :endTime " +
            "GROUP BY sr.member")
    StudyRecordProjection findTotalUsageTimeByMember(@Param("member") Member member,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
}
