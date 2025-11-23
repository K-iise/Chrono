package woowa.chrono.domain.member.dto.response;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncreaseUsageTimeResponse {
    private String userId;
    private Duration usageTime;

    public static IncreaseUsageTimeResponse from(Member member) {
        return IncreaseUsageTimeResponse.builder()
                .userId(member.getUserId())
                .usageTime(member.getUsageTime())
                .build();
    }
}
