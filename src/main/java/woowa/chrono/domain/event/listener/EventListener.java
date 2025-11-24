package woowa.chrono.domain.event.listener;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.config.jda.service.DiscordService;
import woowa.chrono.domain.event.dto.request.RegisterEventRequest;
import woowa.chrono.domain.event.dto.response.RegisterEventResponse;
import woowa.chrono.domain.event.service.EventRecordService;
import woowa.chrono.domain.event.service.EventService;

@Component
public class EventListener extends ListenerAdapter {

    private final EventService eventService;
    private final EventRecordService eventRecordService;
    private final DiscordService discordService;

    public EventListener(EventService eventService, EventRecordService eventRecordService,
                         DiscordService discordService) {
        this.eventService = eventService;
        this.eventRecordService = eventRecordService;
        this.discordService = discordService;
    }

    // 디스코드 서버에서 새로운 이벤트를 DB에 등록하기 위한 이벤트.
    @Override
    public void onScheduledEventCreate(ScheduledEventCreateEvent event) {
        try {
            RegisterEventRequest request = RegisterEventRequest.from(event);
            RegisterEventResponse response = eventService.registerEvent(request);
            
            String message = String.format(
                    "<@%s>님이 새로운 이벤트 **%s**을(를) 등록했습니다.",
                    request.getAdminId(), request.getTitle()
            );
            discordService.sendMessageToChannel(response.getChannelId(), message);
        } catch (ChronoException e) {
            String creatorId = event.getScheduledEvent().getCreatorId();
            var user = event.getJDA().getUserById(creatorId);
            if (user != null) {
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage(e.getMessage()).queue()
                );
            }
        }

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

    // 이벤트 포럼에 팔로우한 경우(이벤트에 참가하기 위한 이벤트)
    @Override
    public void onThreadMemberJoin(ThreadMemberJoinEvent event) {
        String userid = event.getMember().getId();
        ThreadChannel threadChannel = event.getThread();
        String location = threadChannel.getJumpUrl();

        TextChannel recordChannel = event.getGuild().getTextChannelById("1440644403398967376");

        try {
            // 이벤트 참여를 기록한다.
            eventRecordService.participateEvent(userid, location);
            System.out.println("userid = " + userid);
            System.out.println("location = " + location);

            // 사용자의 텍스트 채널에 이벤트 참여 확인 메시지를 보낸다.
            if (recordChannel != null) {
                recordChannel.sendMessage(
                        event.getMember().getAsMention() + "님 **" + threadChannel.getName()
                                + "** 이벤트를 참여합니다.").queue();
            }
        } catch (RuntimeException e) {
            recordChannel.sendMessage(e.getMessage()).queue();
        }
    }
}
