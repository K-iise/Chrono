package woowa.chrono.config.jda.service;

import java.time.Duration;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.domain.event.StudyAutoEndedEvent;

@Service
public class DiscordService {
    private final JDA jda;

    public DiscordService(@Lazy JDA jda) {
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


    // 특정 텍스트 채널에 메시지 전송
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

    // 개인 텍스트 채널 생성
    public TextChannel createPersonalChannel(Guild guild, long userId, String userName) {

        long allow = Permission.VIEW_CHANNEL.getRawValue()
                | Permission.MESSAGE_SEND.getRawValue();

        return guild.createTextChannel(userName)
                .addPermissionOverride(guild.getPublicRole(), 0L, allow)
                .addMemberPermissionOverride(userId, allow, 0L)
                .complete();
    }

    // 디스코드 역할 변경
    public void updateMemberRole(String guildId, String userId, Role newRole, List<String> removeRoles) {
        jda.getGuildById(guildId).retrieveMemberById(userId).queue(member -> {
            // 기존 역할 제거
            removeRoles.stream()
                    .filter(roleName -> member.getRoles().stream().anyMatch(r -> r.getName().equals(roleName)))
                    .forEach(roleName -> {
                        Role r = member.getRoles().stream().filter(r2 -> r2.getName().equals(roleName)).findFirst()
                                .orElse(null);
                        if (r != null) {
                            member.getGuild().removeRoleFromMember(member, r).queue();
                        }
                    });

            // 새 역할 추가
            member.getGuild().addRoleToMember(member, newRole).queue();
        });
    }

}