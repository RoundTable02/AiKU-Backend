package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleMemberResDto {
    private Long memberId;
    private Long nickname;
    private MemberProfileResDto memberProfile;
    private int point;

    @QueryProjection
    public ScheduleMemberResDto(Long memberId, Long nickname, MemberProfileResDto memberProfile, int point) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.point = point;
    }
}
