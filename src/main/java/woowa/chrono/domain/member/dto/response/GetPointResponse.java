package woowa.chrono.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPointResponse {
    private String userId;
    private int point;

    public static GetPointResponse from(Member member) {
        return GetPointResponse.builder()
                .userId(member.getUserId())
                .point(member.getPoint())
                .build();
    }
}
