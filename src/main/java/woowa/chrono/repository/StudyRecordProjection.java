package woowa.chrono.repository;

import java.time.Duration;
import woowa.chrono.domain.Member;

public interface StudyRecordProjection {
    Member getMember();         // member 엔티티

    Long getTotalTimeSeconds();

    default Duration getTotalTime() {
        return getTotalTimeSeconds() == null ? Duration.ZERO : Duration.ofSeconds(getTotalTimeSeconds());
    }
}
