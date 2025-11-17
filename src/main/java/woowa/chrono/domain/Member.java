package woowa.chrono.domain;

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

    public void changeGrade(Grade newGrade) {
        this.grade = newGrade;
    }

    public void addPoint(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("포인트는 음수로 추가할 수 없습니다.");
        }
        this.point += amount;
    }

    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    public void addUsageTime(Duration time) {
        this.usageTime = this.usageTime.plus(time);
    }

    public void updateUsageTime(Duration time) {
        this.usageTime = time;
    }

    public void updatePoint(int point) {
        this.point = point;
    }

}
