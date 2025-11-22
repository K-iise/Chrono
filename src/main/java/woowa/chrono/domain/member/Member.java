package woowa.chrono.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.exception.ErrorCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private Grade grade = Grade.NEWBIE;

    @Builder.Default
    @Column(name = "usage_time")
    private Duration usageTime = Duration.ZERO;

    @Builder.Default
    @Column(name = "point")
    int point = 0;

    @Column(name = "channel_id")
    private String channelId;

    public void changeGrade(Grade newGrade) {
        this.grade = newGrade;
    }

    public void addPoint(int point) {
        validatePositivePoint(point);
        this.point += point;
    }

    public void usePoint(int point) {
        validatePositivePoint(point);
        validateEnoughPoint(point);
        this.point -= point;
    }


    public void addUsageTime(Duration usageTime) {
        validatePositiveTime(usageTime);
        this.usageTime = this.usageTime.plus(usageTime);
    }

    public void useUsageTime(Duration usageTime) {
        validatePositiveTime(usageTime);
        validateEnoughTime(usageTime);
        this.usageTime = this.usageTime.minus(usageTime);
    }

    public void updateUsageTime(Duration usageTime) {
        validatePositiveTime(usageTime);
        this.usageTime = usageTime;
    }


    public void updatePoint(int point) {
        validatePositivePoint(point);
        this.point = point;
    }

    private void validatePositivePoint(int point) {
        if (point < 0) {
            throw new ChronoException(ErrorCode.INVALID_POINT);
        }
    }

    private void validateEnoughPoint(int point) {
        if (this.point < point) {
            throw new ChronoException(ErrorCode.LACK_POINT);
        }
    }

    private void validatePositiveTime(Duration time) {
        if (time.isNegative()) {
            throw new ChronoException(ErrorCode.INVALID_TIME);
        }
    }

    private void validateEnoughTime(Duration time) {
        if (this.usageTime.minus(time).isNegative()) {
            throw new ChronoException(ErrorCode.LACK_TIME);
        }
    }

}
