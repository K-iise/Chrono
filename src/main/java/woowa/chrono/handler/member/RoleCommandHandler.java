package woowa.chrono.handler.member;

import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;

@Component
public class RoleCommandHandler implements CommandHandler {

    private final MemberService memberService;

    public RoleCommandHandler(MemberService memberService) {
        this.memberService = memberService;
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
    public void handle(SlashCommandInteractionEvent event) {
        String userId = event.getOption("user").getAsUser().getId();
        Role role = event.getOption("role").getAsRole();
        Grade grade = Grade.fromRoleName(role.getName());
        memberService.updateMemberGrade(userId, grade);
        
        event.reply(event.getOption("user").getAsUser().getAsMention() +
                "님의 등급이 " + grade.getDisplayName() + "으로 변경되었습니다.").queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "등급을 변경할 사용자", true),
                new OptionData(OptionType.ROLE, "role", "변경할 등급", true));
    }
}
