package woowa.chrono.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Grade;
import woowa.chrono.handler.CommandHandler;

@Component
public class CommandListener extends ListenerAdapter {
    private final Map<String, CommandHandler> handlerMap;

    public CommandListener(List<CommandHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getName, h -> h));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandHandler handler = handlerMap.get(event.getName());

        if (handler == null) {
            event.reply("지원하지 않는 명령어입니다.").queue();
            return;
        }

        handler.handle(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        handlerMap.values().forEach(handler -> {
            CommandData cd = Commands.slash(handler.getName(), handler.getDescription())
                    .addOptions(handler.getOptions().toArray(new OptionData[0]))
                    .addSubcommands(handler.getSubcommands());
            // 관리자인 경우만 Permission 설정
            if (handler.requiredGrade() == Grade.ADMIN) {
                cd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
            }

            commandData.add(cd);
        });

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
