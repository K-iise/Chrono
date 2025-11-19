package woowa.chrono.service;

import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.exception.ErrorCode;
import woowa.chrono.repository.MemberRepository;

@Service
@Transactional
public class MemberService {

    // 포인트로 구매 가능한 시간 단위 (1시간 = 1000포인트)
    private static final int POINT_PER_HOUR = 1000;

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member registerMember(Member member) {
        validateDuplication(member);
        return memberRepository.save(member);
    }

    public Member updateMemberGrade(String userId, Grade grade) {
        Member found = findMemberOrThrow(userId);
        found.changeGrade(grade);
        return found;
    }

    public Duration getUsageTime(String userId) {
        Member found = findMemberOrThrow(userId);
        return found.getUsageTime();
    }

    public int getPoint(String userId) {
        Member found = findMemberOrThrow(userId);
        return found.getPoint();
    }

    public Member increaseUsageTime(String adminId, String userId, int time) {
        validatePositiveTime(time);
        requireAdmin(adminId);
        Member member = findMemberOrThrow(userId);

        member.addUsageTime(Duration.ofMinutes(time));
        return member;
    }

    public Member updateUsageTime(String adminId, String userId, int time) {
        validatePositiveTime(time);
        requireAdmin(adminId);
        Member member = findMemberOrThrow(userId);

        member.updateUsageTime(Duration.ofMinutes(time));
        return member;

    }

    public Member increasePoint(String adminId, String userId, int point) {
        validatePositivePoint(point);
        requireAdmin(adminId);
        Member member = findMemberOrThrow(userId);

        member.addPoint(point);
        return member;
    }


    public Member updatePoint(String adminId, String userId, int point) {
        validatePositivePoint(point);
        requireAdmin(adminId);
        Member member = findMemberOrThrow(userId);

        member.updatePoint(point);
        return member;
    }

    public Member purchaseUsageTime(String userId, int point) {
        if (point % POINT_PER_HOUR != 0) {
            throw new IllegalArgumentException("[ERROR] 포인트의 구매 단위는 " + POINT_PER_HOUR + "입니다.");
        }

        Member member = findMemberOrThrow(userId);
        member.usePoint(point);
        int requiredTimes = point / POINT_PER_HOUR;
        member.addUsageTime(Duration.ofHours(requiredTimes));

        return member;
    }

    private void validatePositiveTime(int time) {
        if (time <= 0) {
            throw new IllegalArgumentException(ErrorCode.INVALID_TIME.getMessage());
        }
    }

    private void validatePositivePoint(int point) {
        if (point < 0) {
            throw new IllegalArgumentException(ErrorCode.INVALID_POINT.getMessage());
        }
    }

    private void validateDuplication(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalStateException(ErrorCode.DUPLICATE_MEMBER.getMessage());
        }
    }

    public Member findMemberOrThrow(String userId) {
        return memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.MEMBER_NOT_FOUND.getMessage())
        );
    }

    public void requireAdmin(String adminId) {
        Member admin = memberRepository.findByUserId(adminId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.ADMIN_NOT_FOUND.getMessage())
        );
        if (admin.getGrade() != Grade.ADMIN) {
            throw new IllegalStateException(ErrorCode.NOT_ADMIN.getMessage());
        }
    }


}
