package woowa.chrono.domain.study.handler;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.service.MemberService;
import woowa.chrono.domain.study.dto.request.EndStudyRequest;
import woowa.chrono.domain.study.dto.request.StartStudyRequest;
import woowa.chrono.domain.study.dto.response.EndStudyResponse;
import woowa.chrono.domain.study.dto.response.StartStudyResponse;
import woowa.chrono.domain.study.service.StudyRecordService;

@Component
public class RecordCommandHandler implements CommandHandler {

    private final StudyRecordService studyRecordService;
    private final MemberService memberService;

    public RecordCommandHandler(StudyRecordService studyRecordService, MemberService memberService) {
        this.studyRecordService = studyRecordService;
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public String getDescription() {
        return "공부 기록 관련 명령어";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }

        switch (subCommand) {
            case "start" -> handleStart(event);
            case "end" -> handleEnd(event);
            default -> event.reply("알 수 없는 명령어입니다.").queue();
        }
    }

    private void handleStart(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        String userId = event.getUser().getId();

        try {
            StartStudyRequest request = StartStudyRequest.builder().userId(userId).build();

            StartStudyResponse response = studyRecordService.startStudy(request);
            String usageTime = DurationUtils.format(response.getUsageTime());
            String message = event.getUser().getAsMention() + "님이 공부를 시작합니다.\n"
                    + "잔여 이용 시간 : " + usageTime;

            // 개인 DM 응답
            event.getHook().sendMessage(message).setEphemeral(true).queue();

            // 개인 텍스트 채널 응답
            event.getGuild().getTextChannelById(response.getChannelId()).sendMessage(message).queue();
        } catch (ChronoException e) {
            event.getHook().sendMessage(e.getMessage()).queue();
        }
    }

    private void handleEnd(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        String userId = event.getUser().getId();

        try {
            EndStudyRequest request = EndStudyRequest.builder().userId(userId).build();
            EndStudyResponse response = studyRecordService.endStudy(request);
            String message = event.getUser().getAsMention() + "님이 공부를 종료했습니다.\n" +
                    "이용한 시간 : " + DurationUtils.format(response.getStudiedDuration());

            // 개인 DM 응답
            event.getHook().sendMessage(message).queue();

            // 개인 텍스트 채널 응답
            event.getGuild().getTextChannelById(response.getChannelId()).sendMessage(message).queue();

        } catch (ChronoException e) {
            event.getHook().sendMessage(e.getMessage()).queue();
        }

    }

    @Override
    public List<OptionData> getOptions() {
        return CommandHandler.super.getOptions();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(new SubcommandData("start", "공부 기록을 시작합니다."),
                new SubcommandData("end", "공부 기록을 종료합니다."));
    }

    @Override
    public Grade requiredGrade() {
        return CommandHandler.super.requiredGrade();
    }
}
