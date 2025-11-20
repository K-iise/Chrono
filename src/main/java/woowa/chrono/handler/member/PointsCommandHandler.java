package woowa.chrono.handler.member;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Member;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;
import woowa.chrono.util.DurationUtils;

@Component
public class PointsCommandHandler implements CommandHandler {
    private final MemberService memberService;

    public PointsCommandHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "points";
    }

    @Override
    public String getDescription() {
        return "포인트 관련 명령어 (일반 사용자 및 관리자용)";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String group = event.getSubcommandGroup();
        String subCommand = event.getSubcommandName();

        if (group == null || subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }

        try {
            switch (group) {
                case "user" -> handleUserCommand(event, subCommand);
                case "admin" -> handleAdminCommand(event, subCommand);
                default -> event.reply("알 수 없는 명령어 그룹입니다.").setEphemeral(true).queue();
            }
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private void handleUserCommand(SlashCommandInteractionEvent event, String subCommand) {
        String userId = event.getUser().getId();
        Member member = memberService.findMemberOrThrow(userId);
        String mention = event.getUser().getAsMention();

        switch (subCommand) {
            case "get" ->
                    event.reply(mention + "님이 보유한 포인트는 **" + member.getPoint() + "**입니다.").setEphemeral(true).queue();
            case "use" -> {
                int amount = event.getOption("amount").getAsInt();
                memberService.purchaseUsageTime(userId, amount);
                event.reply(mention + "님의 현재 남은 포인트는 **" + member.getPoint() + "**입니다.\n"
                        + "남은 이용 시간: " + DurationUtils.format(member.getUsageTime())).setEphemeral(true).queue();
            }
            default -> event.reply("알 수 없는 명령어입니다.").setEphemeral(true).queue();
        }
    }

    private void handleAdminCommand(SlashCommandInteractionEvent event, String subCommand) {
        // 관리자 권한 체크
        memberService.requireAdmin(event.getUser().getId());

        String targetUserId = event.getOption("user").getAsUser().getId();
        Member target = memberService.findMemberOrThrow(targetUserId);
        String mention = event.getOption("user").getAsUser().getAsMention();

        switch (subCommand) {
            case "get" ->
                    event.reply(mention + "님이 보유한 포인트는 **" + target.getPoint() + "**입니다.").setEphemeral(true).queue();
            case "add" -> {
                int addPoint = event.getOption("amount").getAsInt();
                memberService.increasePoint(event.getUser().getId(), targetUserId, addPoint);
                event.reply(mention + "님에게 **" + addPoint + "** 포인트가 추가되었습니다").setEphemeral(true)
                        .queue();
            }
            case "set" -> {
                int setPoint = event.getOption("amount").getAsInt();
                memberService.updatePoint(event.getUser().getId(), targetUserId, setPoint);
                event.reply(mention + "님의 포인트를 **" + setPoint + "**로 설정했습니다.").setEphemeral(true).queue();
            }
            default -> event.reply("알 수 없는 명령어입니다.").setEphemeral(true).queue();
        }
    }

    @Override
    public List<SubcommandGroupData> getSubcommandGroups() {
        return List.of(
                new SubcommandGroupData("user", "일반 사용자용 포인트 명령어")
                        .addSubcommands(
                                new SubcommandData("get", "본인 포인트 확인"),
                                new SubcommandData("use", "포인트 사용")
                                        .addOptions(new OptionData(OptionType.INTEGER, "amount",
                                                "포인트 구매 단위는 1000P당 1시간 이용 시간이 충전됩니다.", true))
                        ),
                new SubcommandGroupData("admin", "관리자용 포인트 명령어")
                        .addSubcommands(
                                new SubcommandData("get", "특정 사용자의 포인트 확인")
                                        .addOptions(new OptionData(OptionType.USER, "user", "조회할 사용자", true)),
                                new SubcommandData("add", "특정 사용자에게 포인트 추가")
                                        .addOptions(
                                                new OptionData(OptionType.USER, "user", "포인트를 추가할 사용자", true),
                                                new OptionData(OptionType.INTEGER, "amount", "추가할 포인트", true)
                                        ),
                                new SubcommandData("set", "특정 사용자의 포인트 설정")
                                        .addOptions(
                                                new OptionData(OptionType.USER, "user", "포인트를 설정할 사용자", true),
                                                new OptionData(OptionType.INTEGER, "amount", "설정할 포인트", true)
                                        )
                        )
        );
    }
}
