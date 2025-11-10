package woowa.chrono.domain;

import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberTest {
    @Test
    public void testMemberBuilder_CreatesObjectCorrectly(){
        Member member = Member.builder()
                .userID("123")
                .serverID("456")
                .userName("홍길동")
                .grade(Grade.NEWBIE)
                .point(4000)
                .usageTime(Duration.ZERO)
                .build();

        Assertions.assertThat(member.getUserID()).isEqualTo("123");
        Assertions.assertThat(member.getServerID()).isEqualTo("456");
        Assertions.assertThat(member.getUserName()).isEqualTo("홍길동");
        Assertions.assertThat(member.getGrade()).isEqualTo(Grade.NEWBIE);
        Assertions.assertThat(member.getUsageTime()).isEqualTo(Duration.ZERO);
        Assertions.assertThat(member.getPoint()).isEqualTo(4000);
    }
}
