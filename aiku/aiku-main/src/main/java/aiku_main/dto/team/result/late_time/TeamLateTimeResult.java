package aiku_main.dto.team.result.late_time;

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
public class TeamLateTimeResult {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;
    private int lateTime;
    private boolean isTeamMember;

    @QueryProjection
    public TeamLateTimeResult(
            Long memberId,
            String nickname,
            MemberProfileResDto memberProfile,
            int lateTime,
            Status isTeamMember
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.lateTime = lateTime;
        this.isTeamMember = isTeamMember == ALIVE
                ? true
                : false;
    }
}
