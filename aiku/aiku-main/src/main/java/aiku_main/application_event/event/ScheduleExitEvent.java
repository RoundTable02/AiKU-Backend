package aiku_main.application_event.event;

import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.ScheduleMemberValue;
import common.domain.value_reference.ScheduleValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleExitEvent {

    private MemberValue member;
    private ScheduleMemberValue scheduleMember;
    private ScheduleValue schedule;

    public ScheduleExitEvent(Member member, ScheduleMember scheduleMember, Schedule schedule) {
        this.member = new MemberValue(member);
        this.scheduleMember = new ScheduleMemberValue(scheduleMember);
        this.schedule = new ScheduleValue(schedule);
    }
}
