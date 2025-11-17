package woowa.chrono.handler.member;

import java.time.Duration;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;

@Component
public class UsageTimeCommandHandler implements CommandHandler {

    private final MemberService memberService;

    public UsageTimeCommandHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "times";
    }

    @Override
    public String getDescription() {
        return "이용시간 관련 명령어";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }

    }

    private void handleGet(SlashCommandInteractionEvent event) {
        String userId = event.getOption("user").getAsUser().getId();
        Duration time = memberService.getUsageTime(userId);

    }

    @Override
    public List<OptionData> getOptions() {
        return CommandHandler.super.getOptions();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("get", "사용자의 이용 시간을 조회합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "조회할 사용자", false)
                        ),
                new SubcommandData("add", "사용자의 이용 시간을 추가합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "추가할 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "추가할 시간", true)
                        ),
                new SubcommandData("set", "사용자의 이용 시간을 직접 수정합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "대상 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "설정할 시간", true)
                        )
        );
    }

    @Override
    public Grade requiredGrade() {
        return CommandHandler.super.requiredGrade();
    }
}
