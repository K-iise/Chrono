package woowa.chrono.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberResponse {
    private String userId;
    private Grade grade;

    public static UpdateMemberResponse from(Member member) {
        return UpdateMemberResponse.builder()
                .userId(member.getUserId())
                .grade(member.getGrade())
                .build();
    }
}
