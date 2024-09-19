package aiku_main.application_event.event;

import common.domain.Schedule;
import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.ScheduleValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleExitEvent {
    private MemberValue member;
    private ScheduleValue schedule;

    public ScheduleExitEvent(Member member, Schedule schedule) {
        this.member = new MemberValue(member);
        this.schedule = new ScheduleValue(schedule);
    }
}
