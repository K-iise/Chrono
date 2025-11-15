package woowa.chrono.service;

import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.MemberRepository;

@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 등록 저장소에 저장된다.")
    public void registrationMemberTest() {
        // given
        Member member = Member.builder().userId("1234").userName("홍길동").build();

        // when
        Member saved = memberService.registerMember(member);

        // then
        Assertions.assertThat(memberRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("사용자의 등급을 변경합니다.")
    public void updateMemberGradeTest() {
        // given
        Member member = Member.builder().userId("123").userName("홍길동").build();
        memberRepository.save(member);

        // when
        Member found = memberService.updateMemberGrade("123", Grade.REGULAR);

        // then
        Assertions.assertThat(found.getGrade()).isEqualTo(Grade.REGULAR);
    }

    @Test
    @DisplayName("사용자의 남은 이용시간을 조회합니다.")
    public void getUsageTimeTest() {
        // given
        Member member = Member.builder().userId("12").userName("홍길동").usageTime(Duration.ZERO).build();
        memberRepository.save(member);

        // when
        Duration found = memberService.getUsageTime(member.getUserId());

        // then
        Assertions.assertThat(found).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("사용자의 보유 포인트를 조회합니다.")
    public void getPointTest() {
        // given
        Member member = Member.builder().userId("1").userName("홍길동").point(1000).build();
        memberRepository.save(member);

        // when
        int found = memberService.getPoint(member.getUserId());

        // then
        Assertions.assertThat(found).isEqualTo(1000);
    }

    @Test
    @DisplayName("ADMIN 사용자가 REGULAR 사용자의 이용 시간을 정상적으로 추가한다.")
    public void increaseUsageTimeSuccessTest() {
        // given
        Member admin = Member.builder()
                .userId("admin")
                .grade(Grade.ADMIN)
                .build();

        Member member = Member.builder()
                .userId("user1")
                .grade(Grade.REGULAR)
                .usageTime(Duration.ofMinutes(0))
                .build();

        memberRepository.save(admin);
        memberRepository.save(member);

        // when
        int addTime = 100; // 분 단위라고 가정
        Member updated = memberService.increaseUsageTime(admin.getUserId(), member.getUserId(), addTime);

        // then
        Assertions.assertThat(updated.getUsageTime())
                .isEqualTo(Duration.ofMinutes(addTime));
    }


    @Test
    @DisplayName("REGULAR 사용자가 이용 시간을 추가하면 예외가 발생한다.")
    public void increaseUsageTimeForbiddenTest() {
        // given
        Member regular = Member.builder()
                .userId("11")
                .grade(Grade.REGULAR)
                .build();

        Member member = Member.builder()
                .userId("12")
                .grade(Grade.REGULAR)
                .usageTime(Duration.ZERO)
                .build();

        memberRepository.save(regular);
        memberRepository.save(member);

        // when & then
        Assertions.assertThatThrownBy(() ->
                memberService.increaseUsageTime(regular.getUserId(), member.getUserId(), 100)
        ).isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("존재하지 않는 사용자의 이용 시간 추가 시 예외 발생")
    public void increaseUsageTimeUserNotFoundTest() {
        // given
        Member admin = Member.builder()
                .userId("admin")
                .grade(Grade.ADMIN)
                .build();

        memberRepository.save(admin);

        // when & then
        Assertions.assertThatThrownBy(() ->
                memberService.increaseUsageTime(admin.getUserId(), "no-user", 100)
        ).isInstanceOf(IllegalArgumentException.class);
    }

}

