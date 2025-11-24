package woowa.chrono.domain.event.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.member.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterEventResponse {
    private String channelId;
    private String eventTitle;

    public static RegisterEventResponse from(Event event, Member admin) {
        return RegisterEventResponse.builder()
                .channelId(admin.getChannelId())
                .eventTitle(event.getTitle()).build();
    }
}
