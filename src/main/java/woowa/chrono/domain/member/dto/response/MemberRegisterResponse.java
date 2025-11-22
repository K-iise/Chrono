package woowa.chrono.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterResponse {
    private String userId;
    private String userName;
    private String channelId;

    public static MemberRegisterResponse from(Member member) {
        return MemberRegisterResponse.builder()
                .userId(member.getUserId())
                .userName(member.getUserName())
                .channelId(member.getChannelId())
                .build();
    }
}
