package aiku_main.dto.team.result.betting_odds;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import common.domain.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static common.domain.Status.ALIVE;

@Getter
@Setter
@NoArgsConstructor
public class TeamBettingResult {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;
    private long odds;
    private boolean isTeamMember;

    @QueryProjection
    public TeamBettingResult(
            Long memberId,
            String nickname,
            MemberProfileResDto memberProfile,
            long odds,
            Status isTeamMember
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.odds = odds;
        this.isTeamMember = isTeamMember == ALIVE
                ? true
                : false;
    }
}
