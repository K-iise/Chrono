package woowa.chrono.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncreaseUsageTimeRequest {
    private String adminId;
    private String userId;
    private int usageTime;
}
