package map.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.member.Member;
import common.domain.schedule.ScheduleMember;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleMemberResDto {

    private Long memberId;
    private String nickname;
    private MemberProfileDto memberProfile;
    private boolean isArrive;
    private boolean isPaidMember;

    @QueryProjection
    public ScheduleMemberResDto(Long memberId, String nickname, MemberProfileDto memberProfile, LocalDateTime arriveTime, boolean isPaidMember) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.isArrive = (arriveTime == null) ? false : true;
        this.isPaidMember = isPaidMember;
    }
}
