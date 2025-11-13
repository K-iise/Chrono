package woowa.chrono.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import woowa.chrono.domain.Grade;
import woowa.chrono.domain.Member;
import woowa.chrono.repository.MemberRepository;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member registerMember(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
        }
        member.setGrade(Grade.NEWBIE);
        member.setUsageTime(Duration.ZERO);
        member.setPoint(0);
        memberRepository.save(member);
        return member;
    }
}
