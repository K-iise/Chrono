package woowa.chrono.handler;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface CommandHandler {
    String getName();

    String getDescription();
    
    void handle(SlashCommandInteractionEvent event);

    default List<OptionData> getOptions() {
        return Collections.emptyList();
    }
}
