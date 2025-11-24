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
import woowa.chrono.domain.event.dto.request.ParticipateEventRequest;
import woowa.chrono.domain.event.dto.request.RegisterEventRequest;
import woowa.chrono.domain.event.dto.response.ParticipateEventResponse;
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
        try {
            ParticipateEventRequest request = ParticipateEventRequest.builder().userId(userid).location(location)
                    .build();

            // 이벤트 참여를 기록한다.
            ParticipateEventResponse response = eventRecordService.participateEvent(request);
            TextChannel recordChannel = event.getGuild().getTextChannelById(response.getChannelId());

            // 사용자의 텍스트 채널에 이벤트 참여 확인 메시지를 보낸다.
            if (recordChannel != null) {
                String message = String.format(
                        "<@%s>님이 **%s** 이벤트를 참여합니다.",
                        userid, event.getScheduledEvent().getName()
                );
                recordChannel.sendMessage(message).queue();
            }

        } catch (ChronoException e) {
            String userId = event.getScheduledEvent().getCreatorId();
            var user = event.getJDA().getUserById(userId);
            if (user != null) {
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage(e.getMessage()).queue()
                );
            }
        }
    }

    // 이벤트 포럼에 팔로우한 경우(이벤트에 참가하기 위한 이벤트)
    @Override
    public void onThreadMemberJoin(ThreadMemberJoinEvent event) {
        String userid = event.getMember().getId();
        ThreadChannel threadChannel = event.getThread();
        String location = threadChannel.getJumpUrl();

        try {
            ParticipateEventRequest request = ParticipateEventRequest.builder().userId(userid).location(location)
                    .build();
            // 이벤트 참여를 기록한다.
            ParticipateEventResponse response = eventRecordService.participateEvent(request);

            TextChannel recordChannel = event.getGuild().getTextChannelById(response.getChannelId());

            // 사용자의 텍스트 채널에 이벤트 참여 확인 메시지를 보낸다.
            if (recordChannel != null) {
                String message = String.format(
                        "<@%s>님이 **%s** 이벤트를 참여합니다.",
                        userid, response.getTitle()
                );
                recordChannel.sendMessage(message).queue();
            }
        } catch (ChronoException e) {
            String userId = event.getMember().getId();
            var user = event.getJDA().getUserById(userId);
            if (user != null) {
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage(e.getMessage()).queue()
                );
            }
        }
    }
}
