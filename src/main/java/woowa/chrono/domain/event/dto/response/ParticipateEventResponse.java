package woowa.chrono.domain.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.event.EventRecord;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipateEventResponse {
    private String userId;
    private String channelId;
    private String title;
    private String location;

    public static ParticipateEventResponse from(EventRecord eventRecord) {
        return ParticipateEventResponse.builder()
                .userId(eventRecord.getMember().getUserId())
                .channelId(eventRecord.getMember().getChannelId())
                .title(eventRecord.getEvent().getTitle())
                .location(eventRecord.getEvent().getEventLocation()).build();
    }
}
