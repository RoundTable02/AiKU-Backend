package aiku_main.dto.schedule;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ScheduleOwnerResDto {

    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;

    @QueryProjection
    public ScheduleOwnerResDto(Long memberId, String nickname, MemberProfileResDto memberProfile) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
    }
}
