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

    @Embedded
    private RacingValue racing;

    public RacingPointLog(Long memberId, int pointAmount, String description, PointLogStatus pointLogStatus, RacingValue racing) {
        super(memberId, pointAmount, description, pointLogStatus);
        this.racing = racing;
    }
}
