package common.domain.log;

import common.domain.value_reference.RacingValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "RACING")
@Entity
public class RacingPointLog extends PointLog{

    @Column(name = "racingPointLogId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RacingValue racing;
}
