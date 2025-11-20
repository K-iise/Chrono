package woowa.chrono.handler.studyrecord;

import java.time.Duration;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.StudyRecordService;
import woowa.chrono.util.DurationUtils;

@Component
public class StudyTimeCommandHandler implements CommandHandler {

    private final StudyRecordService studyRecordService;

    public StudyTimeCommandHandler(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    @Override
    public String getName() {
        return "usage";
    }

    @Override
    public String getDescription() {
        return "이용 시간 조회";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }

        try {
            switch (subCommand) {
                case "week" -> handleWeek(event);
                case "month" -> handleMonth(event);
                case "year" -> handleYear(event);
                default -> event.reply("알 수 없는 명령어입니다.").setEphemeral(true).queue();
            }
        } catch (RuntimeException e) {
            event.reply(e.getMessage()).queue();
        }
    }

    private void handleWeek(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String mention = event.getUser().getAsMention();
        Duration usageTime = studyRecordService.getWeeklyUsageTime(userId);
        event.reply(mention + "님의 주간 이용 시간은 **" + DurationUtils.format(usageTime) + "** 입니다,").queue();
    }

    private void handleMonth(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String mention = event.getUser().getAsMention();
        Duration usageTime = studyRecordService.getMonthlyUsageTime(userId);
        event.reply(mention + "님의 월간 이용 시간은 **" + DurationUtils.format(usageTime) + "** 입니다,").queue();
    }

    private void handleYear(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String mention = event.getUser().getAsMention();
        Duration usageTime = studyRecordService.getYearlyUsageTime(userId);
        event.reply(mention + "님의 연간 이용 시간은 **" + DurationUtils.format(usageTime) + "** 입니다,").queue();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("week", "사용자의 주간 이용 시간을 조회합니다."),
                new SubcommandData("month", "사용자의 월간 이용 시간을 조회합니다."),
                new SubcommandData("year", "사용자의 연간 이용 시간을 조회합니다.")
        );
    }
}
