package woowa.chrono.Listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
}
