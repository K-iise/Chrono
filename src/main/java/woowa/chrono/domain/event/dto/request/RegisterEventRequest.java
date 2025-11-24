package woowa.chrono.domain.event.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import woowa.chrono.domain.event.Event;
import woowa.chrono.domain.member.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterEventRequest {
    private String adminId;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Event toEntity(Member admin) {
        return Event.builder()
                .admin(admin)
                .title(title)
                .content(content)
                .eventLocation(location)
                .startTime(startTime)
                .endTime(endTime).build();
    }

    public static RegisterEventRequest from(ScheduledEventCreateEvent event) {
        return RegisterEventRequest.builder()
                .adminId(event.getScheduledEvent().getCreatorId())
                .title(event.getScheduledEvent().getName())
                .content(event.getScheduledEvent().getDescription())
                .location(event.getScheduledEvent().getLocation())
                .startTime(event.getScheduledEvent().getStartTime().toLocalDateTime())
                .endTime(event.getScheduledEvent().getEndTime().toLocalDateTime()).build();
    }
}
