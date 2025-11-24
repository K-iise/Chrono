package woowa.chrono.domain.study.dto.response;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.member.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartStudyResponse {
    private String userId;
    private String channelId;
    private Duration usageTime;
    private LocalDateTime startTime;

    public static StartStudyResponse from(Member member, LocalDateTime startTime) {
        return StartStudyResponse.builder()
                .userId(member.getUserId())
                .channelId(member.getChannelId())
                .usageTime(member.getUsageTime())
                .startTime(startTime)
                .build();
    }
}
