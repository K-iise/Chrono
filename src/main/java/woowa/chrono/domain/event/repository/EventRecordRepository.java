package woowa.chrono.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.event.EventRecord;
import woowa.chrono.domain.member.Member;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {
    boolean existsByEventAndMember(Event event, Member member);
}
