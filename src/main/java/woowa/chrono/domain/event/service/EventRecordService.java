package woowa.chrono.domain.event.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.event.EventRecord;
import woowa.chrono.domain.event.repository.EventRecordRepository;
import woowa.chrono.domain.event.repository.EventRepository;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.repository.MemberRepository;

@Service
public class EventRecordService {
    private final EventRecordRepository eventRecordRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventRecordService(EventRecordRepository eventRecordRepository, EventRepository eventRepository,
                              MemberRepository memberRepository) {
        this.eventRecordRepository = eventRecordRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 특정 유저 ID와 이벤트 위치를 통해서 이벤트 참여를 기록합니다.
     *
     * @param userId
     * @param eventLocation
     */
    public void participateEvent(String userId, String eventLocation) {
        validateDuplication(userId, eventLocation);

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 회원이 아닙니다."));

        Event event = eventRepository.findByEventLocation(eventLocation)
                .orElseThrow(() -> new IllegalStateException("등록된 이벤트가 아닙니다."));

        EventRecord eventRecord = EventRecord.builder()
                .member(member)
                .event(event)
                .participationTime(LocalDateTime.now()).build();

        eventRecordRepository.save(eventRecord);
    }

    /**
     * 특정 회원의 이벤트 참여 여부를 확인합니다.
     *
     * @param userId
     * @param eventLocation
     */
    private void validateDuplication(String userId, String eventLocation) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 회원이 아닙니다."));
        Event event = eventRepository.findByEventLocation(eventLocation)
                .orElseThrow(() -> new IllegalStateException("등록된 이벤트가 아닙니다."));
        if (eventRecordRepository.existsByEventAndMember(event, member)) {
            throw new IllegalStateException("이미 이벤트에 참여했습니다.");
        }
    }
}
