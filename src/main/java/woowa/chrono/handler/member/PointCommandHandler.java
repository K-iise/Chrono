package woowa.chrono.handler.member;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;
import woowa.chrono.util.DurationUtils;

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
            case "get" -> handleGet(event);
            case "add" -> handleAdd(event);
            case "use" -> handleUse(event);
            case "set" -> handleSet(event);
            default -> event.reply("알 수 없는 명령어입니다.").queue();
        }
    }

    private void handleGet(SlashCommandInteractionEvent event) {
        String userId = event.getOption("user").getAsUser().getId();
        int point = memberService.getPoint(userId);
        event.reply(event.getOption("user").getAsUser().getAsMention() + "님이 보유한 포인트는 " +
                point + "입니다.").queue();
    }

    private void handleAdd(SlashCommandInteractionEvent event) {
        String callerId = event.getUser().getId();
        Grade callerGrade = memberService.findMemberOrThrow(callerId).getGrade();

        if (callerGrade != Grade.ADMIN) {
            event.reply("이 명령어는 관리자만 사용할 수 있습니다.").setEphemeral(true).queue();
            return;
        }

        String adminId = event.getUser().getId();
        String userId = event.getOption("user").getAsUser().getId();
        int addPoint = event.getOption("amount").getAsInt();
        Member member = memberService.increasePoint(adminId, userId, addPoint);

        event.reply(event.getOption("user").getAsUser().getAsMention() +
                        "님에게 **" + addPoint + "** 포인트가 추가되었습니다.\n" +
                        "현재 포인트: " + member.getPoint())
                .queue();
    }

    private void handleUse(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        int usePoint = event.getOption("amount").getAsInt();
        Member member = memberService.purchaseUsageTime(userId, usePoint);

        String formatted = DurationUtils.format(member.getUsageTime());

        event.reply(event.getUser().getAsMention() +
                "님의 현재 남은 포인트는 **" + member.getPoint() + "** 입니다.\n" +
                "남은 이용 시간: " + formatted).queue();
    }

    private void handleSet(SlashCommandInteractionEvent event) {
        String callerId = event.getUser().getId();
        Grade callerGrade = memberService.findMemberOrThrow(callerId).getGrade();

        if (callerGrade != Grade.ADMIN) {
            event.reply("이 명령어는 관리자만 사용할 수 있습니다.").setEphemeral(true).queue();
            return;
        }

        String adminId = event.getUser().getId();
        String userId = event.getOption("user").getAsUser().getId();
        int updatePoint = event.getOption("amount").getAsInt();
        Member member = memberService.updatePoint(adminId, userId, updatePoint);

        event.reply(event.getOption("user").getAsUser().getAsMention() +
                "님의 포인트가 **" + updatePoint + "**(으)로 설정되었습니다.\n" +
                "현재 포인트: " + member.getPoint()).queue();
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
                new SubcommandData("use", "포인트 사용")
                        .addOptions(
                                new OptionData(OptionType.INTEGER, "amount", "포인트 구매 단위는 1000P당 1시간 이용 시간이 충전됩니다.",
                                        true)
                        ),
                new SubcommandData("set", "포인트 설정")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "대상 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "설정할 값", true)
                        )
        );
    }
}
