package woowa.chrono.Listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import woowa.chrono.service.StudyRecordService;

@Component
public class StudyRecordListener extends ListenerAdapter {
    private final StudyRecordService studyRecordService;

    public StudyRecordListener(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }
}
