package woowa.chrono.domain.event.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.exception.ErrorCode;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.event.dto.request.RegisterEventRequest;
import woowa.chrono.domain.event.dto.response.RegisterEventResponse;
import woowa.chrono.domain.event.repository.EventRepository;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.repository.MemberRepository;
import woowa.chrono.domain.study.repository.StudyRecordProjection;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventService(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    // 이벤트 등록
    public RegisterEventResponse registerEvent(RegisterEventRequest request) {
        Member admin = findAdmin(request.getAdminId());
        validateEventTime(request.getStartTime(), request.getEndTime());
        Event event = request.toEntity(admin);
        eventRepository.save(event);
        return RegisterEventResponse.from(event, admin);
    }


    // 이벤트 기간 동안 학습 기록 조회
    @Transactional(readOnly = true)
    public List<StudyRecordProjection> summaryStudyEvent(String location) {
        Event event = eventRepository.findByEventLocation(location)
                .orElseThrow(() -> new ChronoException(ErrorCode.EVENT_NOT_FOUND));

        return eventRepository.findStudySummaryByEvent(
                event.getId(),
                event.getStartTime(),
                event.getEndTime()
        );
    }

    // 관리자 검증
    private Member findAdmin(String adminId) {
        Member member = memberRepository.findByUserId(adminId)
                .orElseThrow(() -> new ChronoException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getGrade() != Grade.ADMIN) {
            throw new ChronoException(ErrorCode.NOT_ADMIN);
        }

        return member;
    }

    // 이벤트 시간 검증
    private void validateEventTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new ChronoException(ErrorCode.INVALID_EVENT_TIME);
        }
    }
}
