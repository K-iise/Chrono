package woowa.chrono.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Member;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.service.MemberService;

@Component
public class MemberListener extends ListenerAdapter {

    private final Map<String, CommandHandler> handlerMap;

    private final MemberService memberService;

    public MemberListener(MemberService memberService, List<CommandHandler> handlers) {
        this.memberService = memberService;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getName, h -> h));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        if (user.isBot()) {
            return;
        }
        String[] messageArray = message.getContentRaw().split(" ");

        if (messageArray[0].equalsIgnoreCase("/명령어")) {
            String test = "test";
            textChannel.sendMessage(test).queue();
        }
    }

    // 멤버 등록 이벤트
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();
        String userid = user.getId();
        String username = user.getName();

        Member member = Member.builder().userId(userid).userName(username).build();
        memberService.registerMember(member);
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
                    .addOptions(handler.getOptions().toArray(new OptionData[0]));
            commandData.add(cd);
        });

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

}
