package woowa.chrono.domain.member.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.config.jda.service.DiscordService;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.service.MemberService;

@Slf4j
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
        return "setup";
    }

    @Override
    public String getDescription() {
        return "서버 주인을 최초 관리자로 등록합니다.";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        var user = event.getUser();
        try {
            memberService.registerAdmin(user.getId(), user.getName(), null);

            // Discord 개인 채널 생성
            TextChannel channel = discordService.createPersonalChannel(
                    event.getGuild(),
                    user.getIdLong(),
                    user.getName()
            );

            // 생성된 채널 ID DB 업데이트
            memberService.updateChannelId(user.getId(), channel.getId());
            event.reply(user.getAsMention() + "님을 관리자로 등록했습니다.").queue();

        } catch (ChronoException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public Grade requiredGrade() {
        return null; // 권한 체크 없음
    }

    @Override
    public List<OptionData> getOptions() {
        return CommandHandler.super.getOptions();
    }
}
