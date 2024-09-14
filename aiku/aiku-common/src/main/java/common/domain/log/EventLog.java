package common.domain.log;

import common.domain.value_reference.BettingValue;
import common.domain.value_reference.EventValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "EVENT")
@Entity
public class EventLog extends PointLog{

    @Embedded
    private EventValue event;
}
