package common.domain.log;

import common.domain.Betting;
import common.domain.value_reference.BettingValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "BETTING")
@Entity
public class BettingPointLog extends PointLog{

    @Embedded
    private BettingValue betting;
}
