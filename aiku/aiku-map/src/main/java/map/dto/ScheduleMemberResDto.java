package map.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleMemberResDto {

    private Long memberId;
    private String nickname;
    private MemberProfileDto memberProfile;
    private boolean isArrive;

    @QueryProjection
    public ScheduleMemberResDto(Long memberId, String nickname, MemberProfileDto memberProfile, LocalDateTime arriveTime) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.isArrive = (arriveTime == null) ? false : true;
    }
}
