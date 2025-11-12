package woowa.chrono.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.MemberRepository;

@DataJpaTest
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 등록 저장소에 저장된다.")
    public void registrationMemberTest() {
        // given
        Member member = Member.builder().userId("123").userName("홍길동").build();

        // when
        Member saved = memberService.registerMember(member);

        // then
        Assertions.assertThat(memberRepository.findById(saved.getId())).isPresent();
    }

}
