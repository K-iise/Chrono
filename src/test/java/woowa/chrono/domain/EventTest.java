package woowa.chrono.domain;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    @DisplayName("이벤트 빌더 생성 테스트")
    public void testEventBuilder_CreatesObjectCorrectly() {
        Event event = Event.builder()
                .eventId("12")
                .content("이벤트 개설")
                .admin(Member.builder().build())
                .startTime(LocalDateTime.MIN)
                .endTime(LocalDateTime.MAX)
                .build();

        Assertions.assertThat(event.getEventId()).isEqualTo("12");
        Assertions.assertThat(event.getContent()).isEqualTo("이벤트 개설");
        Assertions.assertThat(event.getStartTime()).isEqualTo(LocalDateTime.MIN);
        Assertions.assertThat(event.getEndTime()).isEqualTo(LocalDateTime.MAX);
    }
}
