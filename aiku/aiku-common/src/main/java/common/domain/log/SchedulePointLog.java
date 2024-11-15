package common.domain.log;

import common.domain.value_reference.ScheduleValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "SCHEDULE")
@Entity
public class SchedulePointLog extends PointLog{

    @Embedded
    private ScheduleValue schedule;

    public SchedulePointLog(Long memberId, int pointAmount, String description, PointLogStatus pointLogStatus, ScheduleValue schedule) {
        super(memberId, pointAmount, description, pointLogStatus);
        this.schedule = schedule;
    }
}
