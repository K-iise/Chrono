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
public class AdminRegisterResponse {
    String userId;
    String userName;
    String channelId;
    Grade grade;

    public static AdminRegisterResponse from(Member member) {
        return AdminRegisterResponse.builder()
                .userId(member.getUserId())
                .userName(member.getUserName())
                .channelId(member.getChannelId())
                .grade(member.getGrade())
                .build();
    }
}
