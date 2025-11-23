package woowa.chrono.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRegisterRequest {
    String userId;
    String userName;
    String channelId;

    public Member toEntity() {
        return Member.builder()
                .userId(userId)
                .userName(userName)
                .channelId(channelId)
                .build();
    }
}
