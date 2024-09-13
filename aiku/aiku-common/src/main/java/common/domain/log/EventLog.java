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

    @Column(name = "eventLogId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EventValue event;
}
