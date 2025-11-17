package woowa.chrono.repository;

import java.time.Duration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.StudyRecord;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    Optional<StudyRecord> saveStudyTime(String userId, Duration studiedDuration);
}
