package aiku_main.repository.dto;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import common.domain.Status;
import lombok.Getter;

import static common.domain.Status.ALIVE;

@Getter
public class TeamBettingResultMemberDto {
    private Long memberId;
    private String nickName;
    private MemberProfileResDto memberProfile;
    private boolean isWinner;
    private boolean isTeamMember;

    @QueryProjection

    public TeamBettingResultMemberDto(Long memberId, String nickName, MemberProfileResDto memberProfile, boolean isWinner, Status teamMemberStatus) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.memberProfile = memberProfile;
        this.isWinner = isWinner;
        this.isTeamMember = teamMemberStatus == ALIVE ? true : false;
    }
}
