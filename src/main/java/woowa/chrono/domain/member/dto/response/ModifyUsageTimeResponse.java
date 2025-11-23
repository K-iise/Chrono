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
public class ModifyUsageTimeResponse {
    private String userId;
    private Duration usageTime;

    public static ModifyUsageTimeResponse from(Member member) {
        return ModifyUsageTimeResponse.builder()
                .userId(member.getUserId())
                .usageTime(member.getUsageTime())
                .build();
    }
}
