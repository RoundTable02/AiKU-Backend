package aiku_main.dto;

import common.domain.TeamMember;
import lombok.Getter;

@Getter
public class TeamMemberResDto {
    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;

    public TeamMemberResDto(TeamMember teamMember) {
        this.memberId = teamMember.getId();
        this.nickname = teamMember.getMember().getNickname();
        this.memberProfile = new MemberProfileResDto(teamMember.getMember().getProfile());
    }
}
