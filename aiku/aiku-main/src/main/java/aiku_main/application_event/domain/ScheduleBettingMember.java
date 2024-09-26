package aiku_main.application_event.domain;

import aiku_main.dto.MemberProfileResDto;
import common.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBettingMember {
    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;

    public ScheduleBettingMember(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.memberProfile = new MemberProfileResDto(member.getProfile());
    }
}
