package woowa.chrono.domain;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private String userID;
    private String serverID;
    private String userName;
    private Grade grade;
    private Duration usageTime;
    int point;
}
