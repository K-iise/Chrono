package woowa.chrono.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Event;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.EventRepository;
import woowa.chrono.repository.MemberRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventService(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    // 등록한 이벤트를 DB에 저장하는 기능
    public void registerEvent(String adminId, String title, String content, LocalDateTime startTime,
                              LocalDateTime endTime) {
        Member admin = memberRepository.findByUserId(adminId)
                .orElseThrow(() -> new IllegalStateException("등록된 관리자가 아닙니다."));

        Event event = Event.builder().admin(admin).title(title).content(content).startTime(startTime).endTime(endTime)
                .build();

        eventRepository.save(event);
    }
}
