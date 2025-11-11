package woowa.chrono.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.annotations.SQLJoinTableRestriction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Member;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 등록 및 확인 테스트")
    public void addMemberTest(){
        Member member = Member.builder().userId("123").userName("홍길동").build();
        memberRepository.save(member);

        Member found = memberRepository.findById(member.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUserName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("멤버 등급 수정 테스트")
    public void modifiedGradeTest(){

    }
}
