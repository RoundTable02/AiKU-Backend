package common.domain.value_reference;

import common.domain.schedule.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class ScheduleValue {

    @Column(name = "scheduleId")
    private Long id;

    public ScheduleValue(Schedule schedule) {
        this.id = schedule.getId();
    }
}
