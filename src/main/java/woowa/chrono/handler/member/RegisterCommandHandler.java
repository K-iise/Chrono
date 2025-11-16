package woowa.chrono.handler.member;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;

@Component
public class RegisterCommandHandler implements CommandHandler {

    private final MemberService memberService;

    public RegisterCommandHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "사용자를 등록합니다.";
    }

    @Override
    public Grade requiredGrade() {
        return Grade.ADMIN;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String memberId = event.getOption("user").getAsUser().getId();
        String memberName = event.getOption("user").getAsUser().getName();

        Member member = Member.builder()
                .userId(memberId)
                .userName(memberName)
                .build();

        memberService.registerMember(member);
        event.reply("등록 완료!").queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "등록할 사용자", true));
    }
}
