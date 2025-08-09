package aiku_main.dto.schedule.result.racing;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RacingResult {

    private RacingResultMember firstRacer;
    private RacingResultMember secondRacer;
    private int pointAmount;
    private Long winnerId; //memberId

    @QueryProjection
    public RacingResult(RacingResultMember firstRacer, RacingResultMember secondRacer, int pointAmount, Long winnerId) {
        this.firstRacer = firstRacer;
        this.secondRacer = secondRacer;
        this.pointAmount = pointAmount;
        this.winnerId = winnerId;
    }
}
