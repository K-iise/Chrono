package woowa.chrono.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Test
    @DisplayName("이벤트 등록 및 조회 테스트")
    public void addEventTest(){
        // given
        Member admin = Member.builder()
                .userId("123")
                .grade(Grade.ADMIN)
                .build();
        Event event = Event.builder()
                .eventId("123")
                .admin(admin)
                .startTime(LocalDateTime.MIN)
                .endTime(LocalDateTime.MAX)
                .build();

        memberRepository.save(admin);

        // when
        eventRepository.save(event);
        Optional<Event> found = eventRepository.findById(event.getId());

        // then
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getEventId()).isEqualTo(event.getEventId());
        Assertions.assertThat(found.get().getStartTime()).isEqualTo(LocalDateTime.MIN);
        Assertions.assertThat(found.get().getEndTime()).isEqualTo(LocalDateTime.MAX);
    }
}
