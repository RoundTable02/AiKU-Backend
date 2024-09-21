package aiku_main.application_event.publisher;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.application_event.event.ScheduleOpenEvent;
import common.domain.Schedule;
import common.domain.ScheduleMember;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduleEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishScheduleExitEvent(Member member, ScheduleMember scheduleMember, Schedule schedule){
        ScheduleExitEvent event = new ScheduleExitEvent(member, scheduleMember, schedule);
        publisher.publishEvent(event);
    }

    public void publishScheduleOpenEvent(Long scheduleId){
        ScheduleOpenEvent event = new ScheduleOpenEvent(scheduleId);
        publisher.publishEvent(event);
    }

    public void publishScheduleAutoCloseEvent(Long scheduleId){
        ScheduleAutoCloseEvent event = new ScheduleAutoCloseEvent(scheduleId);
        publisher.publishEvent(event);
    }

    public void publishScheduleCloseEvent(Schedule schedule){
        ScheduleCloseEvent event = new ScheduleCloseEvent(schedule);
        publisher.publishEvent(event);
    }
}
