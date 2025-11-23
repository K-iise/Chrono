package woowa.chrono.domain.member.handler;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.dto.request.GetPointRequest;
import woowa.chrono.domain.member.dto.request.ModifyPointRequest;
import woowa.chrono.domain.member.dto.response.GetPointResponse;
import woowa.chrono.domain.member.dto.response.ModifyPointResponse;
import woowa.chrono.domain.member.service.MemberService;

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
        } catch (ChronoException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private void handleUserCommand(SlashCommandInteractionEvent event, String subCommand) {
        event.deferReply(true).queue();

        String userId = event.getUser().getId();
        String mention = event.getUser().getAsMention();

        try {
            switch (subCommand) {
                case "get" -> {
                    GetPointRequest request = GetPointRequest.builder().userId(userId).build();
                    GetPointResponse response = memberService.getPoint(request);

                    event.getHook().sendMessage(
                            mention + "님이 보유한 포인트는 **" + response.getPoint() + "**입니다."
                    ).queue();
                }

                case "use" -> {
                    int amount = event.getOption("amount").getAsInt();
                    Member member = memberService.purchaseUsageTime(userId, amount);

                    event.getHook().sendMessage(
                            mention + "님의 현재 남은 포인트는 **" + member.getPoint() + "**입니다.\n"
                                    + "남은 이용 시간: " + DurationUtils.format(member.getUsageTime())
                    ).queue();
                }

                default -> event.getHook().sendMessage("알 수 없는 명령어입니다.").queue();
            }

        } catch (ChronoException e) {
            event.getHook().sendMessage(e.getMessage()).queue();
        }
    }

    private void handleAdminCommand(SlashCommandInteractionEvent event, String subCommand) {
        event.deferReply(true).queue();

        String adminId = event.getUser().getId();
        String targetUserId = event.getOption("user").getAsUser().getId();
        String mention = event.getOption("user").getAsUser().getAsMention();

        switch (subCommand) {
            case "get" -> {
                GetPointRequest request = GetPointRequest.builder().userId(targetUserId).build();
                GetPointResponse response = memberService.getPoint(request);
                event.getHook().sendMessage(mention + "님이 보유한 포인트는 **" + response.getPoint() + "**입니다.").queue();
            }
            case "add" -> {
                int addPoint = event.getOption("amount").getAsInt();
                ModifyPointRequest request = ModifyPointRequest.builder().adminId(adminId)
                        .userId(targetUserId).point(addPoint).build();
                ModifyPointResponse response = memberService.increasePoint(request);
                event.getHook().sendMessage(mention + "님에게 **" + response.getPoint() + "** 포인트가 추가되었습니다").queue();
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
