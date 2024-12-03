package aiku_main.dto.team;

import aiku_main.dto.MemberProfileResDto;
import common.domain.member.Member;
import common.domain.team.TeamMember;
import lombok.Getter;

@Getter
public class TeamMemberResDto {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;

    public TeamMemberResDto(TeamMember teamMember) {
        Member member = teamMember.getMember();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.memberProfile = new MemberProfileResDto(member.getProfile());
    }
}
