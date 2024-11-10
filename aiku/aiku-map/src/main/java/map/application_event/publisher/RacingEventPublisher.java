package map.application_event.publisher;

import lombok.RequiredArgsConstructor;
import map.application_event.domain.RacingInfo;
import map.application_event.event.AskRacingEvent;
import map.application_event.event.MemberArrivalEvent;
import map.application_event.event.RacingStatusNotChangedEvent;
import map.application_event.event.ScheduleCloseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RacingEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishAskRacingEvent(RacingInfo racingInfo){
        AskRacingEvent event = new AskRacingEvent(racingInfo);
        publisher.publishEvent(event);
    }

    public void publishRacingStatusNotChangedEvent(RacingInfo racingInfo){
        RacingStatusNotChangedEvent event = new RacingStatusNotChangedEvent(racingInfo);
        publisher.publishEvent(event);
    }

    public void publishMemberArrivalEvent(Long memberId, Long scheduleId, String scheduleName){
        MemberArrivalEvent event = new MemberArrivalEvent(memberId, scheduleId, scheduleName);
        publisher.publishEvent(event);
    }

    public void publishScheduleCloseEvent(Long scheduleId){
        ScheduleCloseEvent event = new ScheduleCloseEvent(scheduleId);
        publisher.publishEvent(event);
    }

}
