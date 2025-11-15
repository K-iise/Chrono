package woowa.chrono.service;

import java.time.Duration;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member registerMember(Member member) {
        validateDuplication(member);
        return memberRepository.save(member);
    }

    public Member updateMemberGrade(String userId, Grade grade) {
        Member found = memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
        found.changeGrade(grade);
        return found;
    }

    public Duration getUsageTime(String userId) {
        Member found = memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
        return found.getUsageTime();
    }

    public int getPoint(String userId) {
        Member found = memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다,")
        );
        return found.getPoint();
    }

    public Member increaseUsageTime(String adminId, String userId, int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("추가 시간은 0보다 커야 합니다.");
        }

        Member admin = memberRepository.findByUserId(adminId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 관리자입니다.")
        );

        Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        if (admin.getGrade() != Grade.ADMIN) {
            throw new IllegalStateException("관리자가 아닌 경우 이용 시간을 증가 시킬 수 없습니다.");
        }

        member.addUsageTime(Duration.ofMinutes(time));
        return member;
    }

    public Member updateUsageTime(String adminId, String userId, int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("보유 시간은 0보다 커야 합니다.");
        }

        Member admin = memberRepository.findByUserId(adminId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 관리자입니다.")
        );

        Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        if (admin.getGrade() != Grade.ADMIN) {
            throw new IllegalStateException("관리자가 아닌 경우 이용 시간을 수정 시킬 수 없습니다.");
        }
        member.updateUsageTime(Duration.ofMinutes(time));
        return member;

    }

    private void validateDuplication(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
        }
    }
}
