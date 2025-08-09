package aiku_main.dto.schedule.result.betting;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BettingResultMember {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;

    @QueryProjection
    public BettingResultMember(Long memberId, String nickname, MemberProfileResDto memberProfile) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
    }
}
