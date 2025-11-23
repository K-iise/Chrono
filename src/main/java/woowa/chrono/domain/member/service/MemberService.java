package woowa.chrono.domain.member.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.exception.ErrorCode;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.dto.request.AdminRegisterRequest;
import woowa.chrono.domain.member.dto.request.GetPointRequest;
import woowa.chrono.domain.member.dto.request.GetUsageTimeRequest;
import woowa.chrono.domain.member.dto.request.MemberRegisterRequest;
import woowa.chrono.domain.member.dto.request.ModifyPointRequest;
import woowa.chrono.domain.member.dto.request.ModifyUsageTimeRequest;
import woowa.chrono.domain.member.dto.request.UpdateMemberRequest;
import woowa.chrono.domain.member.dto.response.AdminRegisterResponse;
import woowa.chrono.domain.member.dto.response.GetPointResponse;
import woowa.chrono.domain.member.dto.response.GetUsageTimeResponse;
import woowa.chrono.domain.member.dto.response.MemberRegisterResponse;
import woowa.chrono.domain.member.dto.response.ModifyPointResponse;
import woowa.chrono.domain.member.dto.response.ModifyUsageTimeResponse;
import woowa.chrono.domain.member.dto.response.UpdateMemberResponse;
import woowa.chrono.domain.member.repository.MemberRepository;

@Service
@Transactional
public class MemberService {

    @Value("${chrono.point-per-hour}")
    private int POINT_PER_HOUR;

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원 등록
    public MemberRegisterResponse registerMember(MemberRegisterRequest request) {
        Member member = request.toEntity();
        validateDuplication(member);
        memberRepository.save(member);
        return MemberRegisterResponse.from(member);
    }

    // 회원 등급 변경
    public UpdateMemberResponse updateMemberGrade(UpdateMemberRequest request) {
        Member member = findAdminAndMember(request.getAdminId(), request.getUserId());
        member.changeGrade(request.getGrade());
        return UpdateMemberResponse.from(member);
    }

    // 회원 이용 시간 조회
    public GetUsageTimeResponse getUsageTime(GetUsageTimeRequest request) {
        Member member = findMember(request.getUserId(), false);
        return GetUsageTimeResponse.from(member);
    }

    // 회원 포인트 조회
    public GetPointResponse getPoint(GetPointRequest request) {
        Member member = findMember(request.getUserId(), false);
        return GetPointResponse.from(member);
    }

    // 회원 이용 시간 추가 (관리자 권한 필수)
    public ModifyUsageTimeResponse increaseUsageTime(ModifyUsageTimeRequest request) {
        Member member = findAdminAndMember(request.getAdminId(), request.getUserId());
        member.addUsageTime(Duration.ofMinutes(request.getUsageTime()));
        return ModifyUsageTimeResponse.from(member);
    }

    // 회원 이용 시간 수정 (관리자 권한 필수)
    public ModifyUsageTimeResponse updateUsageTime(ModifyUsageTimeRequest request) {
        Member member = findAdminAndMember(request.getAdminId(), request.getUserId());
        member.updateUsageTime(Duration.ofMinutes(request.getUsageTime()));
        return ModifyUsageTimeResponse.from(member);
    }

    // 회원 포인트 추가 (관리자 권한 필수)
    public ModifyPointResponse increasePoint(ModifyPointRequest request) {
        Member member = findAdminAndMember(request.getAdminId(), request.getUserId());
        member.addPoint(request.getPoint());
        return ModifyPointResponse.from(member);
    }

    // 회원 포인트 수정 (관리자 권한 필수)
    public Member updatePoint(String adminId, String userId, int point) {
        Member member = findAdminAndMember(adminId, userId);
        member.updatePoint(point);
        return member;
    }

    // 이용 시간 구매
    public Member purchaseUsageTime(String userId, int point) {
        Member member = findMember(userId, false);
        if (point % POINT_PER_HOUR != 0) {
            throw new ChronoException(ErrorCode.PURCHASE_UNIT);
        }
        if (member.getPoint() < point) {
            throw new ChronoException(ErrorCode.HAVE_POINT);
        }
        member.usePoint(point);
        member.addUsageTime(Duration.ofHours(point / POINT_PER_HOUR));

        return member;
    }

    // 회원 조회 (관리자 검증 옵션)
    public Member findMember(String userId, boolean mustBeAdmin) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ChronoException(ErrorCode.MEMBER_NOT_FOUND));
        if (mustBeAdmin && member.getGrade() != Grade.ADMIN) {
            throw new ChronoException(ErrorCode.NOT_ADMIN);
        }
        return member;
    }

    // 관리자 등록 (최초 실행 시에만 사용)
    public AdminRegisterResponse registerAdmin(AdminRegisterRequest request) {
        if (hasAdmin()) {
            throw new ChronoException(ErrorCode.EXIST_ADMIN);
        }
        Member admin = request.toEntity();
        memberRepository.save(admin);
        return AdminRegisterResponse.from(admin);
    }

    // 관리자 존재 여부 조회
    private boolean hasAdmin() {
        return memberRepository.existsByGrade(Grade.ADMIN);
    }

    // 회원 및 관리자 조회
    private Member findAdminAndMember(String adminId, String userId) {
        findMember(adminId, true); // 관리자 검증
        return findMember(userId, false);
    }


    // 회원 중복 검증
    private void validateDuplication(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new ChronoException(ErrorCode.DUPLICATE_MEMBER);
        }
    }

    // 개인 채널 ID 변경
    public Member updateChannelId(String userId, String channelId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ChronoException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateChannelId(channelId);
        return member;
    }


}
