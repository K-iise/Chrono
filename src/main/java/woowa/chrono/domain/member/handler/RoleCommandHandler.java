package woowa.chrono.domain.member.handler;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.config.jda.service.DiscordService;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class RoleCommandHandler implements CommandHandler {

    private final MemberService memberService;
    private final DiscordService discordService;

    public RoleCommandHandler(MemberService memberService, DiscordService discordService) {
        this.memberService = memberService;
        this.discordService = discordService;
    }

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getDescription() {
        return "사용자의 등급을 변경합니다.";
    }

    @Override
    public Grade requiredGrade() {
        return Grade.ADMIN;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String callerId = event.getUser().getId();
        var targetUserId = event.getOption("user").getAsUser().getId();
        var role = event.getOption("role").getAsRole();
        var newGrade = Grade.fromRoleName(role.getName());
        var guildId = event.getGuild().getId();

        try {
            memberService.updateMemberGrade(callerId, targetUserId, newGrade);

            discordService.updateMemberRole(guildId, targetUserId, role,
                    Grade.getAllRoleNames());

            event.reply(event.getOption("user").getAsUser().getAsMention() +
                    "님의 등급이 **" + newGrade.getDisplayName() + "**(으)로 변경되었습니다.").setEphemeral(true).queue();
        } catch (ChronoException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }

    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "등급을 변경할 사용자", true),
                new OptionData(OptionType.ROLE, "role", "변경할 등급", true));
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of();
    }
}
