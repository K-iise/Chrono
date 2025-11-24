package woowa.chrono.domain.study.dto.response;


import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.domain.study.StudyRecord;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndStudyResponse {
    private String userId;
    private String channelId;
    private Duration studiedDuration;
    private LocalDateTime recordTime;

    public static EndStudyResponse from(StudyRecord studyRecord) {
        return EndStudyResponse.builder()
                .userId(studyRecord.getMember().getUserId())
                .channelId(studyRecord.getMember().getChannelId())
                .studiedDuration(studyRecord.getSessionTime())
                .recordTime(studyRecord.getRecordTime())
                .build();
    }
}
