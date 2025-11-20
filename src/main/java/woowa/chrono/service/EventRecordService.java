package woowa.chrono.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.EventRecord;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.EventRecordRepository;
import woowa.chrono.repository.EventRepository;
import woowa.chrono.repository.MemberRepository;

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
