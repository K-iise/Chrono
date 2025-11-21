package woowa.chrono.config.jda.service;

import java.time.Duration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.domain.event.StudyAutoEndedEvent;

@Service
public class DiscordService {
    private final JDA jda;

    public DiscordService(JDA jda) {
        this.jda = jda;
    }

    @EventListener
    public void handleStudyAutoEndedEvent(StudyAutoEndedEvent event) {
        String userId = event.getUserId();
        Duration studiedDuration = event.getStudiedDuration();
        String channelId = event.getChannelId(); // 이벤트에서 채널 ID 획득

        // 사용자 멘션(<@userId>)을 포함한 메시지 생성
        String message = String.format(
                "<@%s>님의 공부 시간이 모두 만료되어 **자동으로 종료**되었습니다.\n**이용한 시간** : %s",
                userId,
                DurationUtils.format(studiedDuration)
        );

        sendMessageToChannel(channelId, message);
    }


    /**
     * 특정 텍스트 채널 ID에 메시지를 비동기로 전송합니다.
     *
     * @param channelId 메시지를 보낼 텍스트 채널의 ID
     * @param message   전송할 메시지 내용
     */
    private void sendMessageToChannel(String channelId, String message) {
        TextChannel textChannel = jda.getTextChannelById(channelId);

        if (textChannel == null) {
            System.err.println("[DiscordService] 채널을 찾을 수 없습니다. ID: " + channelId);
            return;
        }

        textChannel.sendMessage(message).queue(
                success -> System.out.println("자동 종료 알림 채널 전송 완료: " + channelId),
                failure -> System.err.println("[DiscordService] 메시지 전송 실패 (권한 문제 예상): " + failure.getMessage())
        );
    }

}