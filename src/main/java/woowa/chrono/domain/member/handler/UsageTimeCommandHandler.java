package woowa.chrono.domain.member.handler;

import java.time.Duration;
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
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.dto.request.GetUsageTimeRequest;
import woowa.chrono.domain.member.dto.request.ModifyUsageTimeRequest;
import woowa.chrono.domain.member.dto.response.GetUsageTimeResponse;
import woowa.chrono.domain.member.dto.response.ModifyUsageTimeResponse;
import woowa.chrono.domain.member.service.MemberService;

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
        return "이용시간 관련 명령어(일반 사용자 및 관리자)";
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
                    event.deferReply(true).queue();

                    GetUsageTimeRequest request = GetUsageTimeRequest.builder().userId(userId).build();
                    GetUsageTimeResponse response = memberService.getUsageTime(request);

                    event.getHook().sendMessage(mention + "님이 보유한 이용 시간은 " +
                            DurationUtils.format(response.getUsageTime()) + "입니다.").queue();
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

        try {
            memberService.findMember(adminId, true);
            switch (subCommand) {
                case "get" -> {
                    GetUsageTimeRequest request = GetUsageTimeRequest.builder().userId(targetUserId).build();
                    GetUsageTimeResponse response = memberService.getUsageTime(request);
                    event.getHook().sendMessage(
                                    mention + "님의 현재 잔여 이용 시간은 **" + DurationUtils.format(response.getUsageTime()) + "**입니다.")
                            .queue();
                }
                case "add" -> {
                    int addUsageTime = event.getOption("usagetime").getAsInt();
                    ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder().adminId(adminId)
                            .userId(targetUserId).usageTime(addUsageTime).build();
                    ModifyUsageTimeResponse response = memberService.increaseUsageTime(request);

                    String addedTimeFormatted = DurationUtils.format(Duration.ofMinutes(addUsageTime));
                    String totalTimeFormatted = DurationUtils.format(response.getUsageTime());

                    String message = mention + "님에게 **" + addedTimeFormatted + "**이(가) 추가되었습니다.\n" +
                            "잔여 이용 시간: **" + totalTimeFormatted + "**";

                    event.getHook().sendMessage(message).queue();
                }
                case "set" -> {
                    int setUsageTime = event.getOption("usagetime").getAsInt();
                    ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                            .adminId(adminId).userId(targetUserId).usageTime(setUsageTime).build();
                    ModifyUsageTimeResponse response = memberService.updateUsageTime(request);

                    String message =
                            mention + "님의 이용 시간이 " + DurationUtils.format(response.getUsageTime()) + "으(로) 설정되었습니다.";

                    event.getHook().sendMessage(message).queue();
                }
                default -> event.getHook().sendMessage("알 수 없는 명령어입니다.").queue();
            }
        } catch (ChronoException e) {
            event.getHook().sendMessage(e.getMessage()).queue();
        }

    }


    @Override
    public List<SubcommandGroupData> getSubcommandGroups() {
        return List.of(
                new SubcommandGroupData("user", "일반 사용자용 이용 시간 명령어")
                        .addSubcommands(
                                new SubcommandData("get", "현재 보유하고 있는 이용 시간을 확인합니다.")
                        ),
                new SubcommandGroupData("admin", "관리자용 이용 시간 명령어")
                        .addSubcommands(
                                new SubcommandData("get", "특정 사용자의 남은 이용 시간 확인")
                                        .addOptions(new OptionData(OptionType.USER, "user", "조회할 사용자", true)),
                                new SubcommandData("add", "특정 사용자에게 이용 시간 추가")
                                        .addOptions(
                                                new OptionData(OptionType.USER, "user", "이용 시간을 추가할 사용자", true),
                                                new OptionData(OptionType.INTEGER, "usagetime", "추가할 이용 시간(Minutes)",
                                                        true)
                                        ),
                                new SubcommandData("set", "특정 사용자의 이용 시간 설정")
                                        .addOptions(
                                                new OptionData(OptionType.USER, "user", "이용 시간을 설정할 사용자", true),
                                                new OptionData(OptionType.INTEGER, "usagetime", "설정할 이용 시간(Minutes)",
                                                        true)
                                        )
                        )
        );
    }

    @Override
    public Grade requiredGrade() {
        return CommandHandler.super.requiredGrade();
    }
}
