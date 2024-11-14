package common.domain.value_reference;

import common.domain.Betting;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class BettingValue {

    @Column(name = "bettingId")
    private Long id;

    public BettingValue(Long id) {
        this.id = id;
    }
}
