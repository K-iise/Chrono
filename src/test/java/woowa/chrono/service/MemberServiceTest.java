package woowa.chrono.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.dto.request.GetPointRequest;
import woowa.chrono.domain.member.dto.request.GetUsageTimeRequest;
import woowa.chrono.domain.member.dto.request.MemberRegisterRequest;
import woowa.chrono.domain.member.dto.request.ModifyPointRequest;
import woowa.chrono.domain.member.dto.request.ModifyUsageTimeRequest;
import woowa.chrono.domain.member.dto.request.UpdateMemberRequest;
import woowa.chrono.domain.member.dto.response.GetPointResponse;
import woowa.chrono.domain.member.dto.response.GetUsageTimeResponse;
import woowa.chrono.domain.member.dto.response.MemberRegisterResponse;
import woowa.chrono.domain.member.dto.response.ModifyPointResponse;
import woowa.chrono.domain.member.dto.response.ModifyUsageTimeResponse;
import woowa.chrono.domain.member.dto.response.UpdateMemberResponse;
import woowa.chrono.domain.member.repository.MemberRepository;
import woowa.chrono.domain.member.service.MemberService;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 등록 저장소에 저장된다.")
    public void registrationMemberTest() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder().userId("1234").userName("홍길동").channelId("123")
                .build();

        // when
        MemberRegisterResponse response = memberService.registerMember(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("1234");
        assertThat(response.getUserName()).isEqualTo("홍길동");
        assertThat(response.getChannelId()).isEqualTo("123");
    }

    @Test
    @DisplayName("사용자의 등급을 변경합니다.")
    public void updateMemberGradeTest() {
        // given
        Member admin = Member.builder().userId("1234").grade(Grade.ADMIN).build();
        Member member = Member.builder().userId("123").userName("홍길동").build();
        memberRepository.save(member);
        memberRepository.save(admin);

        // when
        UpdateMemberRequest request = UpdateMemberRequest.builder().adminId(admin.getUserId())
                .userId(member.getUserId())
                .grade(Grade.REGULAR).build();
        UpdateMemberResponse found = memberService.updateMemberGrade(request);

        // then
        assertThat(found.getGrade()).isEqualTo(Grade.REGULAR);
    }

    @Test
    @DisplayName("사용자의 남은 이용시간을 조회합니다.")
    public void getUsageTimeTest() {
        // given
        Member member = Member.builder().userId("20").userName("홍길동").usageTime(Duration.ZERO).build();
        memberRepository.save(member);

        // when
        GetUsageTimeRequest request = GetUsageTimeRequest.builder().userId(member.getUserId()).build();
        GetUsageTimeResponse found = memberService.getUsageTime(request);

        // then
        assertThat(found.getUsageTime()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("사용자의 보유 포인트를 조회합니다.")
    public void getPointTest() {
        // given
        Member member = Member.builder().userId("1").userName("홍길동").point(1000).build();
        memberRepository.save(member);

        // when
        GetPointRequest request = GetPointRequest.builder().userId(member.getUserId()).build();
        GetPointResponse found = memberService.getPoint(request);

        // then
        assertThat(found.getPoint()).isEqualTo(1000);
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
        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                .adminId(admin.getUserId())
                .userId(member.getUserId())
                .usageTime(addTime).build();

        ModifyUsageTimeResponse response = memberService.increaseUsageTime(request);

        // then
        assertThat(response.getUsageTime())
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

        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                .adminId(regular.getUserId()).userId(member.getUserId()).usageTime(100).build();

        // when & then
        Assertions.assertThatThrownBy(() ->
                memberService.increaseUsageTime(request)
        ).isInstanceOf(ChronoException.class);
    }


    @Test
    @DisplayName("존재하지 않는 사용자의 이용 시간 추가 시 예외 발생")
    public void increaseUsageTimeUserNotFoundTest() {
        // given
        Member admin = Member.builder()
                .userId("admin2")
                .grade(Grade.ADMIN)
                .build();

        memberRepository.save(admin);

        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder().adminId(admin.getUserId())
                .userId("no-user").usageTime(100).build();

        // when & then
        Assertions.assertThatThrownBy(() ->
                memberService.increaseUsageTime(request)
        ).isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("ADMIN 사용자가 REGULAR 사용자의 이용 시간을 정상적으로 수정한다.")
    public void updateUsageTimeSuccessTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        Member member = Member.builder().userId("regular").grade(Grade.REGULAR).usageTime(Duration.ofHours(1000))
                .build();

        memberRepository.save(admin);
        memberRepository.save(member);

        // when
        int updateTime = 100;
        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                .adminId(admin.getUserId()).userId(member.getUserId()).usageTime(updateTime).build();
        ModifyUsageTimeResponse updated = memberService.updateUsageTime(request);

        // then
        assertThat(updated.getUsageTime().toMinutes()).isEqualTo(100);
    }

    @Test
    @DisplayName("REGULAR 사용자가 이용 시간을 수정하면 예외가 발생한다.")
    public void updateUsageTimeForbiddenTest() {
        // given
        Member regular = Member.builder().userId("regular").grade(Grade.REGULAR).build();
        Member member = Member.builder().userId("member").grade(Grade.REGULAR).usageTime(Duration.ofHours(1000))
                .build();

        memberRepository.save(regular);
        memberRepository.save(member);

        // when & then
        int updateTime = 100;
        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                .adminId(regular.getUserId()).userId(member.getUserId()).usageTime(updateTime).build();
        Assertions.assertThatThrownBy(
                        () -> memberService.updateUsageTime(request))
                .isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 이용 시간 수정 시 예외 발생")
    public void updateUsageTimeUserNotFoundTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        memberRepository.save(admin);
        int updateTime = 100;
        // when & then
        ModifyUsageTimeRequest request = ModifyUsageTimeRequest.builder()
                .adminId(admin.getUserId()).userId("test1").usageTime(updateTime).build();
        Assertions.assertThatThrownBy(
                        () -> memberService.updateUsageTime(request))
                .isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("ADMIN 사용자가 REGULAR 사용자의 포인트을 정상적으로 추가한다.")
    public void increasePointSuccessTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        Member regular = Member.builder().userId("regular").grade(Grade.REGULAR).build();

        memberRepository.save(admin);
        memberRepository.save(regular);

        // when
        int addpoint = 1000;
        ModifyPointRequest request = ModifyPointRequest.builder().adminId(admin.getUserId()).userId(regular.getUserId())
                .point(addpoint).build();

        ModifyPointResponse response = memberService.increasePoint(request);

        // then
        assertThat(response.getPoint()).isEqualTo(addpoint);
    }

    @Test
    @DisplayName("REGULAR 사용자가 포인트을 추가시키면 예외가 발생한다.")
    public void increasePointForbiddenTest() {
        // given
        Member notAdmin = Member.builder().userId("notAdmin").grade(Grade.REGULAR).build();
        Member regular = Member.builder().userId("regular").grade(Grade.REGULAR).build();

        memberRepository.save(notAdmin);
        memberRepository.save(regular);

        // when & then
        int addpoint = 100;
        ModifyPointRequest request = ModifyPointRequest.builder().adminId(notAdmin.getUserId())
                .userId(regular.getUserId())
                .point(addpoint).build();
        Assertions.assertThatThrownBy(() ->
                        memberService.increasePoint(request))
                .isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 포인트 추가 시 예외 발생")
    public void increasePointUserNotFoundTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        memberRepository.save(admin);

        int addpoint = 100;
        ModifyPointRequest request = ModifyPointRequest.builder().adminId(admin.getUserId()).userId("test")
                .point(addpoint).build();
        // when & then
        Assertions.assertThatThrownBy(() -> memberService.increasePoint(request))
                .isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("ADMIN 사용자가 REGULAR 사용자의 포인트을 정상적으로 수정한다.")
    public void updatePointSuccessTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        Member member = Member.builder().userId("member").grade(Grade.REGULAR).point(1000).build();

        memberRepository.save(admin);
        memberRepository.save(member);

        // when
        int point = 100;
        ModifyPointRequest request = ModifyPointRequest.builder()
                .adminId(admin.getUserId()).userId(member.getUserId()).point(point).build();
        ModifyPointResponse response = memberService.updatePoint(request);

        // then
        Assertions.assertThat(response.getPoint()).isEqualTo(point);
    }

    @Test
    @DisplayName("REGULAR 사용자가 포인트를 수정하면 예외가 발생한다.")
    public void updatePointForbiddenTest() {
        // given
        Member regular = Member.builder().userId("regular").grade(Grade.REGULAR).build();
        Member member = Member.builder().userId("member").grade(Grade.REGULAR).point(1000).build();

        memberRepository.save(regular);
        memberRepository.save(member);

        // when & then
        int point = 100;
        ModifyPointRequest request = ModifyPointRequest.builder()
                .adminId(regular.getUserId()).userId(member.getUserId()).point(point).build();
        Assertions.assertThatThrownBy(() -> memberService.updatePoint(request))
                .isInstanceOf(ChronoException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 포인트 수정 시 예외 발생")
    public void updatePointUserNotFoundTest() {
        // given
        Member admin = Member.builder().userId("admin").grade(Grade.ADMIN).build();
        int point = 100;
        ModifyPointRequest request = ModifyPointRequest.builder()
                .adminId(admin.getUserId()).userId("test").point(point).build();
        // when & then
        Assertions.assertThatThrownBy(() -> memberService.updatePoint(request))
                .isInstanceOf(ChronoException.class);
    }
}

