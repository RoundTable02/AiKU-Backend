package aiku_main.dto.schedule.result.betting;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BettingResult {

    private ScheduleBettingMember bettor;
    private ScheduleBettingMember betee;
    private int pointAmount;

    @QueryProjection
    public BettingResult(ScheduleBettingMember bettor, ScheduleBettingMember betee, int pointAmount) {
        this.bettor = bettor;
        this.betee = betee;
        this.pointAmount = pointAmount;
    }
}
