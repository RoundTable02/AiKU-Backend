package aiku_main.repository.dto;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import common.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamRacingResultMemberDto {
    private Long memberId;
    private String nickName;
    private MemberProfileResDto memberProfile;
    private boolean isWinner;
    private Status isTeamMember;
}
