package woowa.chrono.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.MemberRepository;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

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
