package woowa.chrono.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudyRecordTest {
    @Test
    @DisplayName("공부 기록 빌더 생성 테스트")
    public void testStudyRecordBuilder_CreatesObjectCorrectly() {

        // given
        Member member = new Member();
        LocalDateTime now = LocalDateTime.now();

        // when
        StudyRecord studyRecord = StudyRecord.builder()
                .member(member)
                .sessionTime(Duration.ZERO)
                .recordTime(now)
                .build();

        // then
        Assertions.assertThat(studyRecord.getMember()).isEqualTo(member);
        Assertions.assertThat(studyRecord.getSessionTime()).isEqualTo(Duration.ZERO);
        Assertions.assertThat(studyRecord.getRecordTime()).isEqualTo(now);
    }
}
