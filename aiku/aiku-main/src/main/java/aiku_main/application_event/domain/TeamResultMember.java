package aiku_main.application_event.domain;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamResultMember {
    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;
    private int analysis;
    private boolean isTeamMember;

    @QueryProjection

    public TeamResultMember(Long memberId, String nickname, MemberProfileResDto memberProfile, int analysis, boolean isTeamMember) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.analysis = analysis;
        this.isTeamMember = isTeamMember;
    }
}
