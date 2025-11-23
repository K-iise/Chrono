package woowa.chrono.domain.member.handler;

import java.util.List;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.config.jda.service.DiscordService;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.dto.request.MemberRegisterRequest;
import woowa.chrono.domain.member.dto.response.MemberRegisterResponse;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class RegisterCommandHandler implements CommandHandler {

    private final MemberService memberService;
    private final DiscordService discordService;

    public RegisterCommandHandler(MemberService memberService, DiscordService discordService) {
        this.memberService = memberService;
        this.discordService = discordService;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "관리자가 사용자를 등록합니다.";
    }

    @Override
    public Grade requiredGrade() {
        return Grade.ADMIN;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        try {
            var targetUser = event.getOption("user").getAsUser();

            // 멤버 등록
            MemberRegisterRequest request = MemberRegisterRequest.builder().userId(targetUser.getId())
                    .userName(targetUser.getName())
                    .channelId(null)
                    .build();

            MemberRegisterResponse response = memberService.registerMember(request);

            // Discord 개인 채널 생성
            TextChannel channel = discordService.createPersonalChannel(
                    event.getGuild(),
                    targetUser.getIdLong(),
                    targetUser.getName()
            );

            memberService.updateChannelId(response.getUserId(), channel.getId());
            event.reply(targetUser.getAsMention() + "님을 등록했습니다.").setEphemeral(true).queue();

        } catch (ChronoException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "등록할 사용자", true));
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of();
    }
}
