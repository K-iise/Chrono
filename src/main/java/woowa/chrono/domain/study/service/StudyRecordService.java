package woowa.chrono.domain.study.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.domain.event.StudyAutoEndedEvent;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.repository.MemberRepository;
import woowa.chrono.domain.study.StudyRecord;
import woowa.chrono.domain.study.dto.request.EndStudyRequest;
import woowa.chrono.domain.study.dto.request.StartStudyRequest;
import woowa.chrono.domain.study.dto.response.EndStudyResponse;
import woowa.chrono.domain.study.dto.response.StartStudyResponse;
import woowa.chrono.domain.study.repository.StudyRecordProjection;
import woowa.chrono.domain.study.repository.StudyRecordRepository;

@Service
@Transactional
public class StudyRecordService {
    private final StudyRecordRepository studyRecordRepository;
    private final MemberRepository memberRepository;
    private final StudyRecordService self;
    private final ApplicationEventPublisher eventPublisher;

    private final ConcurrentHashMap<String, LocalDateTime> studyReadyMap;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> futureMap;
    private final ScheduledExecutorService scheduledExecutorService;

    public StudyRecordService(StudyRecordRepository studyRecordRepository,
                              MemberRepository memberRepository, @Lazy StudyRecordService self,
                              ApplicationEventPublisher eventPublisher) {
        this.studyRecordRepository = studyRecordRepository;
        this.memberRepository = memberRepository;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(100);
        this.studyReadyMap = new ConcurrentHashMap<>();
        this.futureMap = new ConcurrentHashMap<>();
        this.self = self;
        this.eventPublisher = eventPublisher;
    }

    // 공부 기록 시작
    @Transactional
    public StartStudyResponse startStudy(StartStudyRequest request) {
        Member member = findMember(request.getUserId());
        validateStart(member);

        LocalDateTime startTime = LocalDateTime.now();
        studyReadyMap.put(request.getUserId(), startTime);

        scheduleAutoEnd(member, member.getChannelId());

        return StartStudyResponse.from(member, startTime);
    }

    // 시작 검증
    private void validateStart(Member member) {
        if (member.getUsageTime().isNegative() || member.getUsageTime().isZero()) {
            throw new IllegalStateException("잔여 이용 시간이 없습니다.");
        }
        if (studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("이미 공부를 시작했습니다.");
        }
    }

    // 자동 종료 추가
    private void scheduleAutoEnd(Member member, String channelId) {
        Runnable endTask = () -> self.autoEndStudy(member.getUserId(), channelId, member.getUsageTime());
        ScheduledFuture<?> future = scheduledExecutorService.schedule(
                endTask,
                member.getUsageTime().getSeconds(),
                TimeUnit.SECONDS
        );
        futureMap.put(member.getUserId(), future);
    }

    // 공부 기록 종료
    @Transactional
    public EndStudyResponse endStudy(EndStudyRequest request) {
        Member member = findMember(request.getUserId());
        validateEnd(member);

        cancelScheduledFuture(request.getUserId());

        Duration studiedDuration = calculateStudiedDuration(member);
        StudyRecord record = saveStudyRecord(member, studiedDuration);

        cleanupAfterEnd(request.getUserId());

        return EndStudyResponse.from(record);
    }

    // 종료 검증
    private void validateEnd(Member member) {
        if (!studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요.");
        }
    }

    // 자동 종료 제거
    private void cancelScheduledFuture(String userId) {
        ScheduledFuture<?> future = futureMap.get(userId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private Duration calculateStudiedDuration(Member member) {
        LocalDateTime start = studyReadyMap.get(member.getUserId());
        Duration duration = Duration.between(start, LocalDateTime.now());
        return duration.compareTo(member.getUsageTime()) > 0 ? member.getUsageTime() : duration;
    }

    private StudyRecord saveStudyRecord(Member member, Duration sessionTime) {
        StudyRecord record = StudyRecord.builder()
                .member(member)
                .sessionTime(sessionTime)
                .recordTime(LocalDateTime.now())
                .build();

        studyRecordRepository.save(record);
        member.useUsageTime(sessionTime);
        return record;
    }

    private void cleanupAfterEnd(String userId) {
        studyReadyMap.remove(userId);
        futureMap.remove(userId);
    }

    // 자동 종료 기능
    @Transactional
    public void autoEndStudy(String userId, String channelId, Duration sessionTime) {
        Member member = findMember(userId);

        studyReadyMap.remove(userId);
        saveStudyRecord(member, sessionTime);
        eventPublisher.publishEvent(new StudyAutoEndedEvent(userId, channelId, sessionTime));
        futureMap.remove(userId);
    }

    // 주간 공부 시간 조회
    public Duration getWeeklyUsageTime(String userId) {
        return getUsageTimeByPeriod(userId, LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
    }

    // 월간 공부 시간 조회
    public Duration getMonthlyUsageTime(String userId) {
        return getUsageTimeByPeriod(userId, LocalDateTime.now().minusMonths(1), LocalDateTime.now());
    }

    // 연간 공부 시간 조회
    public Duration getYearlyUsageTime(String userId) {
        return getUsageTimeByPeriod(userId, LocalDateTime.now().minusYears(1), LocalDateTime.now());
    }

    // 특정 기간 공부 시간 조회
    private Duration getUsageTimeByPeriod(String userId, LocalDateTime start, LocalDateTime end) {
        Member member = findMember(userId);
        StudyRecordProjection result = studyRecordRepository.findTotalUsageTimeByMember(member, start, end);
        return result == null ? Duration.ZERO : result.getTotalTime();
    }

    public boolean endStudyIfActive(String userId) {
        return futureMap.containsKey(userId);
    }

    private Member findMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));
    }

}
