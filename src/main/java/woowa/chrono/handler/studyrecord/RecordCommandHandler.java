package woowa.chrono.handler.studyrecord;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.handler.CommandHandler;

@Component
public class RecordCommandHandler implements CommandHandler {
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

    }

    private void handleEnd(SlashCommandInteractionEvent event) {

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
