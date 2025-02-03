package common.domain.value_reference;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Embeddable
public class ScheduleValue {

    @Column(name = "scheduleId")
    private Long id;
}
