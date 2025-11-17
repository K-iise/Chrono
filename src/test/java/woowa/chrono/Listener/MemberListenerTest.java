package woowa.chrono.Listener;

import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import woowa.chrono.handler.CommandHandler;
import woowa.chrono.repository.MemberRepository;
import woowa.chrono.service.MemberService;

@ExtendWith(MockitoExtension.class)
public class MemberListenerTest {

    @Mock
    GuildMemberJoinEvent joinEvent;

    @Mock
    User user;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberService memberService;

    MemberListener memberListener;

    @Mock
    List<CommandHandler> handlerMap;

    @BeforeEach
    public void setUp() {
        memberListener = new MemberListener(memberService);
    }

    @Test
    @DisplayName("멤버 등록 이벤트 테스트")
    public void registerMemberEventTest() {
        Mockito.when(joinEvent.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn("100");
        Mockito.when(user.getName()).thenReturn("테스트");

        memberListener.onGuildMemberJoin(joinEvent);

        Mockito.verify(memberService).registerMember(
                Mockito.argThat(member ->
                        member.getUserId().equals("100") &&
                                member.getUserName().equals("테스트"))
        );
    }
}
