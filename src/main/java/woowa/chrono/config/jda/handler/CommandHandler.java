package woowa.chrono.config.jda.handler;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import woowa.chrono.domain.member.Grade;

public interface CommandHandler {
    String getName();

    String getDescription();

    void handle(SlashCommandInteractionEvent event);

    default List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }

    default List<SubcommandGroupData> getSubcommandGroups() {
        return Collections.emptyList();
    }

    default Grade requiredGrade() {
        return Grade.REGULAR; // 기본값: 모든 멤버 등급 가능
    }
}
