package woowa.chrono.Listener;

import java.time.LocalDateTime;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.service.EventRecordService;
import woowa.chrono.service.EventService;

@Component
public class EventListener extends ListenerAdapter {

    private final EventService eventService;
    private final EventRecordService eventRecordService;

    public EventListener(EventService eventService, EventRecordService eventRecordService) {
        this.eventService = eventService;
        this.eventRecordService = eventRecordService;
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
        eventService.registerEvent(userId, title, content, location, startTime, endTime);
    }

    // 디스코드 서버에서 이벤트에 참가하기 위한 이벤트.
    @Override
    public void onScheduledEventUserAdd(ScheduledEventUserAddEvent event) {
        String userid = event.getUserId();
        String location = event.getScheduledEvent().getLocation();
        TextChannel recordChannel = event.getGuild().getTextChannelById("1440644403398967376");

        try {
            // 이벤트 참여를 기록한다.
            eventRecordService.participateEvent(userid, location);
            System.out.println("userid = " + userid);
            System.out.println("location = " + location);

            // 사용자의 텍스트 채널에 이벤트 참여 확인 메시지를 보낸다.
            if (recordChannel != null) {
                recordChannel.sendMessage(
                                event.getUser().getAsMention() + "님 **" + event.getScheduledEvent().getName()
                                        + "** 이벤트를 참여합니다.")
                        .queue();
            }
        } catch (RuntimeException e) {
            recordChannel.sendMessage(e.getMessage()).queue();
        }

    }
}
