package woowa.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
