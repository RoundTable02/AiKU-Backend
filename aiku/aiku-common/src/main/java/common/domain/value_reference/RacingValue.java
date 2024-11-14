package common.domain.value_reference;

import common.domain.Racing;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class RacingValue {

    @Column(name = "racingId")
    private Long id;

    public RacingValue(Long id) {
        this.id = id;
    }
}
