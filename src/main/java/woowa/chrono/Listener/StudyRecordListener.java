package woowa.chrono.Listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.StudyRecord;
import woowa.chrono.service.StudyRecordService;
import woowa.chrono.util.DurationUtils;

@Component
public class StudyRecordListener extends ListenerAdapter {
    private final StudyRecordService studyRecordService;

    public StudyRecordListener(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    /**
     * 사용자가 음성 채널에 입장하거나 퇴장할 때 발생하는 이벤트
     *
     * @param event
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        User user = event.getEntity().getUser();
        var userId = user.getId();
        var joinedChannel = event.getChannelJoined();
        var leftChannel = event.getChannelLeft();

        TextChannel recordChannel = event.getGuild().getTextChannelById("1440644403398967376");

        try {
            // 사용자가 새로운 채널에 입장한 경우
            if (joinedChannel != null && leftChannel == null) {
                var channelId = recordChannel.getId();
                studyRecordService.startStudy(userId, channelId);

                // 사용자의 텍스트 채널에 시작 메시지를 보낸다.
                if (recordChannel != null) {
                    recordChannel.sendMessage(user.getAsMention() + "님이 학습을 시작합니다.").queue();
                }
            }

            // 사용자가 채널에 나간 경우
            if (joinedChannel == null && leftChannel != null && studyRecordService.endStudyIfActive(userId)) {
                StudyRecord studyRecord = studyRecordService.endStudy(userId);

                // 사용자의 텍스트 채널에 종료 메시지를 보낸다.
                if (recordChannel != null) {
                    recordChannel.sendMessage(user.getAsMention() + "님의 학습이 종료되었습니다.\n" +
                            "이용한 시간 : " + DurationUtils.format(studyRecord.getSessionTime())).queue();
                }
            }
        } catch (RuntimeException e) {
            recordChannel.sendMessage(e.getMessage()).queue();
        }
    }
}
