package woowa.chrono.service;

import org.springframework.stereotype.Service;
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

    private void validateDuplication(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
        }
    }
}
