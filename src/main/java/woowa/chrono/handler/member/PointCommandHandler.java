package woowa.chrono.handler.member;

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
public class PointCommandHandler implements CommandHandler {

    private final MemberService memberService;

    public PointCommandHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "points";
    }

    @Override
    public String getDescription() {
        return "포인트 관련 명령어";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }

        switch (subCommand) {
            case "get":
                break;
            case "add":
                break;
            case "remove":
                break;
            case "set":
                break;
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return CommandHandler.super.getOptions();
    }

    @Override
    public Grade requiredGrade() {
        return Grade.REGULAR;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("get", "포인트 조회")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "조회할 사용자", false)
                        ),
                new SubcommandData("add", "포인트 추가")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "추가할 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "추가할 양", true)
                        ),
                new SubcommandData("remove", "포인트 차감")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "차감할 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "차감할 양", true)
                        ),
                new SubcommandData("set", "포인트 설정")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "대상 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "설정할 값", true)
                        )
        );
    }
}
