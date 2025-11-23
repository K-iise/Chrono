package woowa.chrono.domain.member.handler;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.config.jda.service.DiscordService;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class InitAdminCommandHandler implements CommandHandler {

    private final MemberService memberService;
    private final DiscordService discordService;

    public InitAdminCommandHandler(MemberService memberService, DiscordService discordService) {
        this.memberService = memberService;
        this.discordService = discordService;

    }

    @Override
    public String getName() {
        return "initAdmin";
    }

    @Override
    public String getDescription() {
        return "서버 주인을 최초 관리자로 등록합니다.";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        try {
            var user = event.getUser(); // 커맨드를 실행한 유저(서버 주인)

            // Discord 개인 채널 생성
            TextChannel channel = discordService.createPersonalChannel(
                    event.getGuild(),
                    user.getIdLong(),
                    user.getName()
            );

            memberService.registerAdmin(user.getId(), user.getName(), channel.getId());
            event.reply(user.getAsMention() + "님을 관리자로 등록했습니다.").queue();

        } catch (ChronoException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public Grade requiredGrade() {
        return null; // 권한 체크 없음
    }
}
