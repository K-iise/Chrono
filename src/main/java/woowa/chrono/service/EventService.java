package woowa.chrono.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.EventRepository;
import woowa.chrono.repository.MemberRepository;
import woowa.chrono.repository.StudyRecordProjection;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventService(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 지정된 관리자(adminId)가 새로운 이벤트를 등록합니다.
     *
     * @param adminId
     * @param title
     * @param content
     * @param location
     * @param startTime
     * @param endTime
     */
    public void registerEvent(String adminId, String title, String content, String location, LocalDateTime startTime,
                              LocalDateTime endTime) {
        Member admin = memberRepository.findByUserId(adminId)
                .orElseThrow(() -> new IllegalStateException("등록된 관리자가 아닙니다."));

        Event event = Event.builder().admin(admin).title(title).content(content).eventLocation(location)
                .startTime(startTime).endTime(endTime)
                .build();

        eventRepository.save(event);
    }

    public List<StudyRecordProjection> summaryStudyEvent(String location) {
        Event event = eventRepository.findByEventLocation(location)
                .orElseThrow(() -> new IllegalStateException("등록된 이벤트가 아닙니다."));
        return eventRepository.findStudySummaryByEvent(event.getId(), event.getStartTime(), event.getEndTime());
    }
}
