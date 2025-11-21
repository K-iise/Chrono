package woowa.chrono.domain.member.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class MemberListener extends ListenerAdapter {

    private final MemberService memberService;

    public MemberListener(MemberService memberService) {
        this.memberService = memberService;

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
