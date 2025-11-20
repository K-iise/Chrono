package woowa.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.EventRecord;
import woowa.chrono.domain.Member;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {
    boolean existsByEventAndMember(Event event, Member member);
}
