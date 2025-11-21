package woowa.chrono.common.util;

import java.time.Duration;

public class DurationUtils {

    public static String format(Duration duration) {
        long seconds = duration.getSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("분 ");
        }
        if (secs > 0 || sb.length() == 0) {
            sb.append(secs).append("초");
        }

        return sb.toString().trim();
    }
}
