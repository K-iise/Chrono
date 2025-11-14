package woowa.chrono.Listener;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.Member;
import woowa.chrono.service.MemberService;

@Component
public class MemberListener extends ListenerAdapter {

    private final MemberService memberService;

    public MemberListener(MemberService memberService) {
        this.memberService = memberService;
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
        switch (event.getName()) {
            case "ping":
                event.reply("**Pong!**").queue();
                break;
            case "role":
                event.reply("**role!**").queue();
                break;
            case "register":
                event.reply("**register!**").queue();
                break;
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandDatas = new ArrayList<>();
        commandDatas.add(
                Commands.slash("ping", "Pong을 해줍니다.")
        );
        commandDatas.add(
                Commands.slash("reply", "Reply를 해줍니다.")
        );
        commandDatas.add(
                Commands.slash("register", "멤버를 등록합니다.")
                        .addOption(OptionType.STRING, "userId", "사용자 ID", true)

        );

        event.getGuild().updateCommands().addCommands(commandDatas).queue();
    }
}
