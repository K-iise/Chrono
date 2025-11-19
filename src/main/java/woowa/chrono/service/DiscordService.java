package woowa.chrono.service;

import java.time.Duration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import woowa.chrono.event.StudyAutoEndedEvent;
import woowa.chrono.util.DurationUtils;

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

        // 채널 메시지 전송 메서드 호출
        sendMessageToChannel(channelId, message);
    }

    // --- 채널 메시지 전송 로직 ---

    /**
     * 특정 텍스트 채널 ID에 메시지를 비동기로 전송합니다.
     *
     * @param channelId 메시지를 보낼 텍스트 채널의 ID
     * @param message   전송할 메시지 내용
     */
    private void sendMessageToChannel(String channelId, String message) {
        // 1. JDA를 통해 채널 ID로 TextChannel 객체를 조회합니다. (동기적 조회)
        TextChannel textChannel = jda.getTextChannelById(channelId);

        if (textChannel == null) {
            // 채널 ID가 잘못되었거나, 봇이 해당 서버에 없거나, 권한이 없어 접근할 수 없는 경우
            System.err.println("[DiscordService] 채널을 찾을 수 없습니다. ID: " + channelId);
            // 필요하다면 이 시점에 DM 전송 로직을 호출하여 사용자에게 개별 알림을 보낼 수도 있습니다.
            return;
        }

        // 2. 메시지를 전송합니다. (비동기 처리)
        textChannel.sendMessage(message).queue(
                success -> System.out.println("자동 종료 알림 채널 전송 완료: " + channelId),
                // 3. 실패 처리 (메시지 쓰기 권한이 없는 경우 등)
                failure -> System.err.println("[DiscordService] 메시지 전송 실패 (권한 문제 예상): " + failure.getMessage())
        );
    }

}