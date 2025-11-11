package woowa.chrono.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import woowa.chrono.domain.Grade;
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
    @Transactional
    @DisplayName("멤버 등급 수정 테스트")
    public void modifiedGradeTest(){
        // given
        Member member = Member.builder().userId("1234").userName("홍길동").grade(Grade.NEWBIE).build();
        memberRepository.save(member);

        // when
        member.setGrade(Grade.REGULAR);

        // then
        Member found = memberRepository.findById(member.getId()).orElse(null);
        assertThat(found.getGrade()).isEqualTo(Grade.REGULAR);
    }

    @Test
    @DisplayName("멤버 삭제 테스트")
    public void deleteMemberTest(){
        // given
        Member member = Member.builder().userId("abcd").userName("홍길동").grade(Grade.NEWBIE).build();
        memberRepository.save(member);

        // when
        memberRepository.delete(member);

        // then
        Optional<Member> found = memberRepository.findById(member.getId());
        Assertions.assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("유저 ID로 멤버 조회 테스트")
    public void findByUserIdTest(){
        // given
        Member member = Member.builder().userId("1234").userName("홍길동").grade(Grade.NEWBIE).build();
        memberRepository.save(member);

        // when
        Optional<Member> found = memberRepository.findByUserId("1234");

        // then
        Assertions.assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("홍길동");
    }
}
