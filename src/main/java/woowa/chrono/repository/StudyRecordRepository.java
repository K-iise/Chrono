package woowa.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.StudyRecord;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
}
