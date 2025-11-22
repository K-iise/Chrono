package woowa.chrono.domain.member.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.domain.member.dto.request.MemberRegisterRequest;
import woowa.chrono.domain.member.dto.response.MemberRegisterResponse;
import woowa.chrono.domain.member.service.MemberService;

@Slf4j
@Component
public class MemberListener extends ListenerAdapter {

    private final MemberService memberService;

    public MemberListener(MemberService memberService) {
        this.memberService = memberService;

    }

    // 자동 멤버 등록 이벤트
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            User user = event.getUser();
            MemberRegisterRequest request = MemberRegisterRequest.builder().userId(user.getId())
                    .userName(user.getName()).build();
            MemberRegisterResponse response = memberService.registerMember(request);
        } catch (ChronoException e) {
            log.info(e.getMessage());
        }

    }


}
