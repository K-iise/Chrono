package woowa.chrono.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.domain.Member;
import woowa.chrono.domain.StudyRecord;
import woowa.chrono.repository.MemberRepository;
import woowa.chrono.repository.StudyRecordRepository;

@Service
@Transactional
public class StudyRecordService {
    private final StudyRecordRepository studyRecordRepository;
    private final MemberRepository memberRepository;

    private ConcurrentHashMap<String, LocalDateTime> studyReadyMap;

    public StudyRecordService(StudyRecordRepository studyRecordRepository, MemberRepository memberRepository) {
        this.studyRecordRepository = studyRecordRepository;
        this.memberRepository = memberRepository;
        studyReadyMap = new ConcurrentHashMap<>();
    }

    public void startStudy(String userId) {

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
    }

    public StudyRecord endStudy(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 멥버가 아닙니다."));

        if (!studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요.");
        }

        LocalDateTime startTime = studyReadyMap.get(member.getUserId());
        LocalDateTime endTime = LocalDateTime.now();

        Duration studiedDuration = Duration.between(startTime, endTime);

        StudyRecord studyRecord = StudyRecord.
                builder().sessionTime(studiedDuration).member(member).recordTime(endTime).build();

        studyRecordRepository.save(studyRecord);
        System.out.println("Before: " + member.getUsageTime());
        member.useUsageTime(studiedDuration);
        System.out.println("After: " + member.getUsageTime());
        studyReadyMap.remove(member.getUserId());

        return studyRecord;
    }
}
