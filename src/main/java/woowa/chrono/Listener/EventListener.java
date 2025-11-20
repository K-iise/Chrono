package woowa.chrono.Listener;

import java.time.LocalDateTime;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.service.EventService;

@Component
public class EventListener extends ListenerAdapter {

    private final EventService eventService;

    public EventListener(EventService eventService) {
        this.eventService = eventService;
    }

    // 디스코드 서버에서 새로운 이벤트를 DB에 등록하기 위한 이벤트.
    @Override
    public void onScheduledEventCreate(ScheduledEventCreateEvent event) {
        String userId = event.getScheduledEvent().getCreatorId();
        String title = event.getScheduledEvent().getName();
        String content = event.getScheduledEvent().getDescription();
        LocalDateTime startTime = event.getScheduledEvent().getStartTime().toLocalDateTime();
        LocalDateTime endTime = event.getScheduledEvent().getEndTime().toLocalDateTime();
        String location = event.getScheduledEvent().getLocation();

        System.out.println("location = " + location);
        eventService.registerEvent(userId, title, content, startTime, endTime);
    }

    // 디스코드 서버에서 이벤트에 참가하기 위한 이벤트.
    @Override
    public void onScheduledEventUserAdd(ScheduledEventUserAddEvent event) {
        String userid = event.getUserId();
        String name = event.getScheduledEvent().getName();
        String detail = event.getScheduledEvent().getDescription();
        String location = event.getScheduledEvent().getLocation();

        System.out.println("userid = " + userid);
        System.out.println("name = " + name);
        System.out.println("detail = " + detail);
        System.out.println("location = " + location);
    }
}
