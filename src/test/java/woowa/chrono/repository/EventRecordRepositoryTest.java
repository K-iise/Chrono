package woowa.chrono.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.EventRecord;
import woowa.chrono.domain.Member;

@DataJpaTest
public class EventRecordRepositoryTest {

    @Autowired
    EventRecordRepository eventRecordRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("멤버 이벤트 참여 테스트")
    public void addEventRecordTest() {
        // given
        Member member = Member.builder()
                .userId("123")
                .userName("홍길동")
                .build();
        Event event = Event.builder()
                .content("테스트 이벤트")
                .admin(member)
                .title("test")
                .eventLocation("test")
                .startTime(LocalDateTime.MIN)
                .endTime(LocalDateTime.MAX)
                .build();

        // when
        memberRepository.save(member);
        eventRepository.save(event);
        EventRecord eventRecord = EventRecord.builder()
                .event(event)
                .member(member)
                .participationTime(LocalDateTime.MAX)
                .build();
        eventRecordRepository.save(eventRecord);

        // then
        Optional<EventRecord> found = eventRecordRepository.findById(eventRecord.getId());
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getParticipationTime()).isEqualTo(LocalDateTime.MAX);
    }
}
