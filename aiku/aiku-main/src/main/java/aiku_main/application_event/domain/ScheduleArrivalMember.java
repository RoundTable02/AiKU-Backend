package aiku_main.application_event.domain;

import aiku_main.dto.MemberProfileResDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleArrivalMember{
    private Long memberId;
    private String nickname;
    private MemberProfileResDto memberProfile;
    private LocalDateTime arrivalTime;
    private int arrivalTimeDiff;

    @QueryProjection
    public ScheduleArrivalMember(Long memberId, String nickname, MemberProfileResDto memberProfile, LocalDateTime arrivalTime, int arrivalTimeDiff) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberProfile = memberProfile;
        this.arrivalTime = arrivalTime;
        this.arrivalTimeDiff = arrivalTimeDiff;
    }
}
