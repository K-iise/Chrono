package woowa.chrono.repository;

import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.domain.StudyRecord;

@DataJpaTest
public class StudyRecordRepositoryTest {

    @Autowired
    StudyRecordRepository studyRecordRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("공부기록 등록 및 확인 테스트")
    public void addStudyRecordTest(){
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
                .recordId("12")
                .member(member)
                .recordTime(now)
                .sessionTime(Duration.ZERO)
                .build();

        // when
        studyRecordRepository.save(studyRecord);
        Optional<StudyRecord> found = studyRecordRepository.findById(studyRecord.getId());

        // then
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getRecordId()).isEqualTo("12");
        Assertions.assertThat(found.get().getRecordTime()).isEqualTo(now);
        Assertions.assertThat(found.get().getSessionTime()).isEqualTo(Duration.ZERO);
    }
}
