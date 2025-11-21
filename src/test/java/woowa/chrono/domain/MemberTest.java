package woowa.chrono.domain;

import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;

public class MemberTest {
    @Test
    @DisplayName("멤버 빌더 생성 테스트")
    public void testMemberBuilder_CreatesObjectCorrectly() {
        Member member = Member.builder()
                .userId("123")
                .userName("홍길동")
                .grade(Grade.NEWBIE)
                .point(4000)
                .usageTime(Duration.ZERO)
                .build();

        Assertions.assertThat(member.getUserId()).isEqualTo("123");
        Assertions.assertThat(member.getUserName()).isEqualTo("홍길동");
        Assertions.assertThat(member.getGrade()).isEqualTo(Grade.NEWBIE);
        Assertions.assertThat(member.getUsageTime()).isEqualTo(Duration.ZERO);
        Assertions.assertThat(member.getPoint()).isEqualTo(4000);
    }
}
