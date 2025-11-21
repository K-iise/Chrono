package woowa.chrono.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.repository.MemberRepository;
import woowa.chrono.domain.study.StudyRecord;
import woowa.chrono.domain.study.repository.StudyRecordProjection;
import woowa.chrono.domain.study.repository.StudyRecordRepository;

@DataJpaTest
public class StudyRecordRepositoryTest {

    @Autowired
    StudyRecordRepository studyRecordRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("공부기록 등록 및 확인 테스트")
    public void addStudyRecordTest() {
        // given
        Member member = Member.builder()
                .userId("123")
                .userName("홍길동")
                .grade(Grade.NEWBIE)
                .usageTime(Duration.ZERO)
                .point(100).build();

        memberRepository.save(member);

        LocalDateTime now = LocalDateTime.now();
        StudyRecord studyRecord = StudyRecord.builder()
                .member(member)
                .recordTime(now)
                .sessionTime(Duration.ZERO)
                .build();

        // when
        studyRecordRepository.save(studyRecord);
        Optional<StudyRecord> found = studyRecordRepository.findById(studyRecord.getId());

        // then
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getRecordTime()).isEqualTo(now);
        Assertions.assertThat(found.get().getSessionTime()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("특정 회원의 이용 내역 조회 테스트")
    public void testFindTotalUsageTime() {
        Member member1 = memberRepository.save(Member.builder().userId("user1").build());

        // member1 기록
        studyRecordRepository.save(StudyRecord.builder()
                .member(member1)
                .sessionTime(Duration.ofHours(2))
                .recordTime(LocalDateTime.of(2025, 11, 1, 10, 0))
                .build());

        studyRecordRepository.save(StudyRecord.builder()
                .member(member1)
                .sessionTime(Duration.ofHours(1))
                .recordTime(LocalDateTime.of(2025, 11, 2, 12, 0))
                .build());

        LocalDateTime start = LocalDateTime.of(2025, 11, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 11, 3, 0, 0);

        StudyRecordProjection result = studyRecordRepository.findTotalUsageTimeByMember(member1, start, end);
        Assertions.assertThat(result.getTotalTime()).isEqualTo(Duration.ofHours(3));
    }
}
