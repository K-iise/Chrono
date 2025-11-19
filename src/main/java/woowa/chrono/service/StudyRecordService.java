package woowa.chrono.service;

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
import woowa.chrono.domain.Member;
import woowa.chrono.domain.StudyRecord;
import woowa.chrono.event.StudyAutoEndedEvent;
import woowa.chrono.repository.MemberRepository;
import woowa.chrono.repository.StudyRecordRepository;

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

        if (member.getUsageTime().isNegative()) {
            throw new IllegalStateException("잔여 이용 시간이 없습니다.");
        }

        if (studyReadyMap.containsKey(userId)) {
            throw new IllegalStateException("이미 공부를 시작했습니다.");
        }

        LocalDateTime startTime = LocalDateTime.now();
        studyReadyMap.put(userId, startTime);

        Runnable endTask = () -> self.autoEndStudy(userId, channelId);

        ScheduledFuture<?> future = scheduledExecutorService.schedule(
                endTask,
                member.getUsageTime().getSeconds(),
                TimeUnit.SECONDS
        );

        futureMap.put(userId, future);

        return member;
    }

    @Transactional
    public StudyRecord autoEndStudy(String userId, String channelId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        ScheduledFuture<?> future = futureMap.get(userId);

        LocalDateTime startTime = studyReadyMap.get(member.getUserId());
        LocalDateTime endTime = LocalDateTime.now();
        Duration studiedDuration = Duration.between(startTime, endTime);

        studyReadyMap.remove(member.getUserId());

        if (studiedDuration.compareTo(member.getUsageTime()) > 0) {
            studiedDuration = member.getUsageTime();
        }

        StudyRecord studyRecord = StudyRecord.
                builder().sessionTime(studiedDuration).member(member).recordTime(endTime).build();

        studyRecordRepository.save(studyRecord);
        member.useUsageTime(studiedDuration);
        eventPublisher.publishEvent(new StudyAutoEndedEvent(userId, channelId, studiedDuration));

        return studyRecord;
    }

    @Transactional
    public StudyRecord endStudy(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멤버가 아닙니다."));

        if (!studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요.");
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
}
