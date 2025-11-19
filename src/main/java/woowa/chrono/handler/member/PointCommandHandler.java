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
        return "포인트 조회, 추가, 사용, 설정 기능을 제공하며, 일부 기능은 관리자만 사용 가능합니다.";
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

    // 사용자가 자신 또는 특정 멤버의 포인트를 조회할 때 발생하는 이벤트
    private void handleGet(SlashCommandInteractionEvent event) {
        try {
            // 선택한 유저(Option)이 있는지 확인한다.
            var optionUser = event.getOption("user");

            // 남의 포인트 조회 시 관리자인지 확인
            if (optionUser != null) {
                memberService.requireAdmin(event.getUser().getId());
            }

            String targetUserId = (optionUser != null) ? optionUser.getAsUser().getId() : event.getUser().getId();
            String mention =
                    (optionUser != null) ? optionUser.getAsUser().getAsMention() : event.getUser().getAsMention();

            // 선택된 유저의 포인트를 조회한다.
            int point = memberService.getPoint(targetUserId);
            event.reply(mention + "님이 보유한 포인트는 " +
                    point + "입니다.").queue();
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    // 사용자가 특정 멤버의 포인트를 부여할 때 발생하는 이벤트
    private void handleAdd(SlashCommandInteractionEvent event) {
        try {
            // 사용자가 관리자인지 확인한다.
            String adminId = event.getUser().getId();
            memberService.requireAdmin(adminId);

            // 관리자인 경우 특정 멤버의 포인트를 추가한다.
            String userId = event.getOption("user").getAsUser().getId();
            int addPoint = event.getOption("amount").getAsInt();
            Member member = memberService.increasePoint(adminId, userId, addPoint);

            event.reply(event.getOption("user").getAsUser().getAsMention() +
                            "님에게 **" + addPoint + "** 포인트가 추가되었습니다.\n" +
                            "현재 포인트: " + member.getPoint()).setEphemeral(true)
                    .queue();
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    // 사용자가 보유한 포인트를 사용할 때 발생하는 이벤트
    private void handleUse(SlashCommandInteractionEvent event) {
        try {
            String userId = event.getUser().getId();
            int usePoint = event.getOption("amount").getAsInt();
            Member member = memberService.purchaseUsageTime(userId, usePoint);

            String formatted = DurationUtils.format(member.getUsageTime());

            event.reply(event.getUser().getAsMention() +
                    "님의 현재 남은 포인트는 **" + member.getPoint() + "** 입니다.\n" +
                    "남은 이용 시간: " + formatted).setEphemeral(true).queue();

        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    // 사용자가 특정 멤버의 포인트를 수정할 때 발생하는 이벤트
    private void handleSet(SlashCommandInteractionEvent event) {
        try {
            // 사용자가 관리자인지 확인한다.
            String adminId = event.getUser().getId();
            memberService.requireAdmin(adminId);

            // 관리자인 경우 포인트를 수정한다.
            String userId = event.getOption("user").getAsUser().getId();
            int updatePoint = event.getOption("amount").getAsInt();
            Member member = memberService.updatePoint(adminId, userId, updatePoint);

            event.reply(event.getOption("user").getAsUser().getAsMention() +
                    "님의 포인트가 **" + updatePoint + "**(으)로 설정되었습니다.\n" +
                    "현재 포인트: " + member.getPoint()).setEphemeral(true).queue();

        } catch (RuntimeException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }

    }

    @Override
    public List<OptionData> getOptions() {
        return CommandHandler.super.getOptions();
    }

    @Override
    public Grade requiredGrade() {
        return CommandHandler.super.requiredGrade();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("get", "본인 또는 다른 사용자의 포인트를 조회합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "조회할 사용자(선택)", false)
                        ),
                new SubcommandData("add", "관리자가 특정 사용자에게 포인트를 추가합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "포인트를 추가할 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "추가할 포인트 양", true)
                        ),
                new SubcommandData("use", "자신의 포인트를 사용하여 이용 시간을 구매합니다.")
                        .addOptions(
                                new OptionData(OptionType.INTEGER, "amount", "포인트 구매 단위는 1000P당 1시간 이용 시간이 충전됩니다.",
                                        true)
                        ),
                new SubcommandData("set", "관리자가 특정 사용자의 포인트를 설정합니다.")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "포인트를 설정할 사용자", true),
                                new OptionData(OptionType.INTEGER, "amount", "설정할 포인트 값", true)
                        )
        );
    }
}
