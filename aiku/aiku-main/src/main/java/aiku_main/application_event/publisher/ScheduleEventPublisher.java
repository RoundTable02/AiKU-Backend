package aiku_main.application_event.publisher;

import aiku_main.application_event.event.ScheduleExitEvent;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.ScheduleValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduleEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Long memberId, Long scheduleId){
        ScheduleExitEvent event = new ScheduleExitEvent(new MemberValue(memberId), new ScheduleValue(scheduleId));
        publisher.publishEvent(event);
    }
}
