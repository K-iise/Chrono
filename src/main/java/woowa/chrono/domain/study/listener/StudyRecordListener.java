package woowa.chrono.domain.study.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.domain.study.dto.request.EndStudyRequest;
import woowa.chrono.domain.study.dto.request.StartStudyRequest;
import woowa.chrono.domain.study.dto.response.EndStudyResponse;
import woowa.chrono.domain.study.dto.response.StartStudyResponse;
import woowa.chrono.domain.study.service.StudyRecordService;

@Component
public class StudyRecordListener extends ListenerAdapter {
    private final StudyRecordService studyRecordService;

    public StudyRecordListener(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    // 사용자가 음성 채널에 입장하거나 퇴장할 때 발생하는 이벤트
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        User user = event.getEntity().getUser();
        var userId = user.getId();
        var joinedChannel = event.getChannelJoined();
        var leftChannel = event.getChannelLeft();

        try {
            // 사용자가 새로운 채널에 입장한 경우
            if (joinedChannel != null && leftChannel == null) {
                StartStudyRequest request = StartStudyRequest.builder().userId(userId).build();
                StartStudyResponse response = studyRecordService.startStudy(request);

                TextChannel recordChannel = event.getGuild().getTextChannelById(response.getChannelId());

                // 사용자의 텍스트 채널에 시작 메시지를 보낸다.
                if (recordChannel != null) {
                    String message = user.getAsMention() + "님이 공부를 시작합니다.\n"
                            + "잔여 이용 시간 : " + response.getUsageTime();
                    recordChannel.sendMessage(message).queue();
                }
            }

            // 사용자가 채널에 나간 경우
            if (joinedChannel == null && leftChannel != null && studyRecordService.endStudyIfActive(userId)) {
                EndStudyRequest request = EndStudyRequest.builder().userId(userId).build();
                EndStudyResponse response = studyRecordService.endStudy(request);
                TextChannel recordChannel = event.getGuild().getTextChannelById(response.getChannelId());

                // 사용자의 텍스트 채널에 종료 메시지를 보낸다.
                if (recordChannel != null) {
                    recordChannel.sendMessage(user.getAsMention() + "님의 학습이 종료되었습니다.\n" +
                            "이용한 시간 : " + DurationUtils.format(response.getStudiedDuration())).queue();
                }
            }
        } catch (ChronoException e) {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(e.getMessage()).queue();
            });
        }
    }
}
