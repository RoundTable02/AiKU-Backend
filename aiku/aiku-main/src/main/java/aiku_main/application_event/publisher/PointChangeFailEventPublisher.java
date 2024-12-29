package aiku_main.application_event.publisher;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.application_event.event.PointChangeFailEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointChangeFailEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(MemberValue member, PointChangeType changeType, int pointAmount, PointChangeReason reason, Long reasonId){
        PointChangeFailEvent event = new PointChangeFailEvent(member, changeType, pointAmount, reason, reasonId);
        publisher.publishEvent(event);
    }

}
