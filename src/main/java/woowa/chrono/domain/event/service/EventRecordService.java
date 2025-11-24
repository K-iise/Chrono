package woowa.chrono.domain.event.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.exception.ErrorCode;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.event.EventRecord;
import woowa.chrono.domain.event.dto.request.ParticipateEventRequest;
import woowa.chrono.domain.event.dto.response.ParticipateEventResponse;
import woowa.chrono.domain.event.repository.EventRecordRepository;
import woowa.chrono.domain.event.repository.EventRepository;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.repository.MemberRepository;

@Service
public class EventRecordService {
    private final EventRecordRepository eventRecordRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventRecordService(EventRecordRepository eventRecordRepository,
                              EventRepository eventRepository,
                              MemberRepository memberRepository) {
        this.eventRecordRepository = eventRecordRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    // 이벤트 참여
    public ParticipateEventResponse participateEvent(ParticipateEventRequest request) {
        Member member = findMemberById(request.getUserId());
        Event event = findEventByLocation(request.getLocation());

        validateNotParticipated(member, event);

        EventRecord eventRecord = EventRecord.builder()
                .member(member)
                .event(event)
                .participationTime(LocalDateTime.now())
                .build();

        eventRecordRepository.save(eventRecord);
        return ParticipateEventResponse.from(eventRecord);
    }

    // 회원 조회
    private Member findMemberById(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ChronoException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 이벤트 조회
    private Event findEventByLocation(String eventLocation) {
        return eventRepository.findByEventLocation(eventLocation)
                .orElseThrow(() -> new ChronoException(ErrorCode.EVENT_NOT_FOUND));
    }

    // 창여 여부 검증
    private void validateNotParticipated(Member member, Event event) {
        if (eventRecordRepository.existsByEventAndMember(event, member)) {
            throw new ChronoException(ErrorCode.EVENT_ALREADY);
        }
    }
}
