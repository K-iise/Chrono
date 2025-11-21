package woowa.chrono.domain.event;

import java.time.Duration;

public class StudyAutoEndedEvent {
    private final String userId;
    private final String channelId;
    private final Duration studiedDuration;

    public StudyAutoEndedEvent(String userId, String channelId, Duration studiedDuration) {
        this.userId = userId;
        this.channelId = channelId;
        this.studiedDuration = studiedDuration;
    }

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public Duration getStudiedDuration() {
        return studiedDuration;
    }
}