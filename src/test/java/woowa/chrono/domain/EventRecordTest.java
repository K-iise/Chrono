package woowa.chrono.domain;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventRecordTest {
    @Test
    @DisplayName("이벤트 기록 빌더 생성 테스트")
    public void testEventRecordBuilder_CreatesObjectCorrectly(){

        Event event = Event.builder().build();
        Member member = Member.builder().build();
        EventRecord eventRecord = EventRecord.builder()
                .event(event)
                .member(member)
                .participationTime(LocalDateTime.MAX)
                .build();

        Assertions.assertThat(eventRecord.getParticipationTime()).isEqualTo(LocalDateTime.MAX);
    }
}
