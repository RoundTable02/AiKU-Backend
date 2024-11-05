package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleMemberResDto {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;
    private int point;
    private boolean isBetee;

    @QueryProjection
    public ScheduleMemberResDto(Long memberId, String nickname, MemberProfileResDto memberProfile, int point, Long beteeId) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.point = point;
        this.isBetee = (beteeId == null)? false : true;
    }
}
