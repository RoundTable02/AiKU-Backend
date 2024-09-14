package aiku_main.dto;

import common.domain.member.Member;
import common.domain.TeamMember;
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
