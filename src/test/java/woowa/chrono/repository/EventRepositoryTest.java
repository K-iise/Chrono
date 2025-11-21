package woowa.chrono.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.EventRecord;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.domain.StudyRecord;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRecordRepository eventRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    @Test
    @DisplayName("이벤트 등록 및 조회 테스트")
    public void addEventTest() {
        // given
        Member admin = Member.builder()
                .userId("123")
                .grade(Grade.ADMIN)
                .build();
        Event event = Event.builder()
                .admin(admin)
                .title("test")
                .startTime(LocalDateTime.MIN)
                .endTime(LocalDateTime.MAX)
                .build();

        memberRepository.save(admin);

        // when
        eventRepository.save(event);
        Optional<Event> found = eventRepository.findById(event.getId());

        // then
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getStartTime()).isEqualTo(LocalDateTime.MIN);
        Assertions.assertThat(found.get().getEndTime()).isEqualTo(LocalDateTime.MAX);
    }

    @Test
    @DisplayName("특정 이벤트에 참여한 멤버들의 총 공부시간 조회 테스트")
    void findStudySummaryByEventTest() {
        // given
        Member admin = memberRepository.save(
                Member.builder().userId("admin").grade(Grade.ADMIN).build()
        );
        Member memberA = memberRepository.save(
                Member.builder().userId("A").grade(Grade.REGULAR).build()
        );
        Member memberB = memberRepository.save(
                Member.builder().userId("B").grade(Grade.REGULAR).build()
        );

        Event event = eventRepository.save(
                Event.builder()
                        .admin(admin)
                        .title("event")
                        .eventLocation("testLocation")
                        .startTime(LocalDateTime.of(2025, 11, 21, 0, 0))
                        .endTime(LocalDateTime.of(2025, 11, 23, 0, 0))
                        .build()
        );

        EventRecord eventRecord1 = EventRecord.builder().event(event).member(memberA)
                .participationTime(LocalDateTime.of(2025, 11, 22, 0, 0)).build();
        EventRecord eventRecord2 = EventRecord.builder().event(event).member(memberB)
                .participationTime(LocalDateTime.of(2025, 11, 22, 0, 0)).build();

        eventRecordRepository.save(eventRecord1);
        eventRecordRepository.save(eventRecord2);

        StudyRecord studyRecord1 = StudyRecord.builder().member(memberA).sessionTime(Duration.ofHours(1))
                .recordTime(LocalDateTime.of(2025, 11, 21, 10, 0)).build();
        StudyRecord studyRecord2 = StudyRecord.builder().member(memberA).sessionTime(Duration.ofHours(2))
                .recordTime(LocalDateTime.of(2025, 11, 22, 10, 0)).build();
        StudyRecord studyRecord3 = StudyRecord.builder().member(memberB).sessionTime(Duration.ofHours(3))
                .recordTime(LocalDateTime.of(2025, 11, 22, 10, 0)).build();

        studyRecordRepository.save(studyRecord1);
        studyRecordRepository.save(studyRecord2);
        studyRecordRepository.save(studyRecord3);

        // when
        List<StudyRecordProjection> results =
                eventRepository.findStudySummaryByEvent(
                        event.getId(),
                        event.getStartTime(),
                        event.getEndTime()
                );

        // then
        Assertions.assertThat(results).hasSize(2);

        StudyRecordProjection aResult = results.stream()
                .filter(r -> r.getMember().equals(memberA))
                .findFirst().orElseThrow();

        StudyRecordProjection bResult = results.stream()
                .filter(r -> r.getMember().equals(memberB))
                .findFirst().orElseThrow();

        Assertions.assertThat(aResult.getTotalTimeSeconds()).isEqualTo(10800L);
        Assertions.assertThat(bResult.getTotalTimeSeconds()).isEqualTo(10800L);
    }
}
