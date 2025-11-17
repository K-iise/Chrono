package woowa.chrono.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Member;
import woowa.chrono.domain.StudyRecord;
import woowa.chrono.repository.StudyRecordRepository;

@Service
public class StudyRecordService {
    private final StudyRecordRepository studyRecordRepository;

    private ConcurrentHashMap<String, LocalDateTime> studyReadyMap;

    public StudyRecordService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
        studyReadyMap = new ConcurrentHashMap<>();
    }

    public void startStudy(String userid) {
        LocalDateTime startTime = LocalDateTime.now();
        studyReadyMap.put(userid, startTime);
    }

    public void endStudy(Member member) {
        if (!studyReadyMap.containsKey(member.getUserId())) {
            throw new IllegalStateException("공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요.");
        }

        LocalDateTime startTime = studyReadyMap.get(member.getUserId());
        LocalDateTime endTime = LocalDateTime.now();

        Duration studiedDuration = Duration.between(startTime, endTime);

        StudyRecord studyRecord = StudyRecord.
                builder().sessionTime(studiedDuration).member(member).recordTime(endTime).build();

        studyRecordRepository.save(studyRecord);

        studyReadyMap.remove(member.getUserId());

    }
}
