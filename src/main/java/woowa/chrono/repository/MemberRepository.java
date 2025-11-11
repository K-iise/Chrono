package woowa.chrono.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woowa.chrono.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
}
