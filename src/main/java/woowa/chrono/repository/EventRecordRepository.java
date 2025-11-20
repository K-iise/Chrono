package woowa.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.EventRecord;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {
}
