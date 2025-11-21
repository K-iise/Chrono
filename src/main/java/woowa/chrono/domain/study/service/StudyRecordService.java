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


    @Transactional
    public Member startStudy(String userId, String channelId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        if (member.getUsageTime().isNegative() || member.getUsageTime().isZero()) {
            throw new IllegalStateException("잔여 이용 시간이 없습니다.");
        }

        if (studyReadyMap.containsKey(userId)) {
            throw new IllegalStateException("이미 공부를 시작했습니다.");
        }

        LocalDateTime startTime = LocalDateTime.now();
        studyReadyMap.put(userId, startTime);

        Runnable endTask = () -> self.autoEndStudy(userId, channelId, member.getUsageTime());

        ScheduledFuture<?> future = scheduledExecutorService.schedule(
                endTask,
                member.getUsageTime().getSeconds(),
                TimeUnit.SECONDS
        );

        futureMap.put(userId, future);

        return member;
    }

    @Transactional
    public void autoEndStudy(String userId, String channelId, Duration sessionTime) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        studyReadyMap.remove(member.getUserId());

        StudyRecord studyRecord = StudyRecord.
                builder().sessionTime(sessionTime).member(member).recordTime(LocalDateTime.now()).build();

        studyRecordRepository.save(studyRecord);
        member.useUsageTime(sessionTime);
        eventPublisher.publishEvent(new StudyAutoEndedEvent(userId, channelId, sessionTime));
        futureMap.remove(userId);

    }

    @Transactional
    public StudyRecord endStudy(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        if (!studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요.");
        }

        if (futureMap.containsKey(userId)) {
            ScheduledFuture<?> future = futureMap.get(userId);
            future.cancel(false);
        }

        LocalDateTime startTime = studyReadyMap.get(member.getUserId());
        LocalDateTime endTime = LocalDateTime.now();

        Duration studiedDuration = Duration.between(startTime, endTime);

        if (studiedDuration.compareTo(member.getUsageTime()) > 0) {
            studiedDuration = member.getUsageTime();
        }
        StudyRecord studyRecord = StudyRecord.
                builder().sessionTime(studiedDuration).member(member).recordTime(endTime).build();

        studyRecordRepository.save(studyRecord);
        member.useUsageTime(studiedDuration);
        studyReadyMap.remove(member.getUserId());
        futureMap.remove(userId);

        return studyRecord;
    }

    public boolean endStudyIfActive(String userId) {
        return futureMap.containsKey(userId);
    }

    // 주간 이용 시간 조회
    public Duration getWeeklyUsageTime(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        LocalDateTime start = LocalDateTime.now().minusWeeks(1);
        LocalDateTime end = LocalDateTime.now();

        return getUsageTime(member, start, end);
    }

    // 월간 이용 시간 조회
    public Duration getMonthlyUsageTime(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();

        return getUsageTime(member, start, end);
    }

    // 연간 이용 시간 조회
    public Duration getYearlyUsageTime(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();

        return getUsageTime(member, start, end);
    }

    // 특정 회원의 이용 시간 조회
    private Duration getUsageTime(Member member, LocalDateTime start, LocalDateTime end) {
        StudyRecordProjection result = studyRecordRepository.findTotalUsageTimeByMember(member, start, end);
        return result == null ? Duration.ZERO : result.getTotalTime();
    }

}
