package login.application_event.publisher;

import common.domain.member.Member;
import login.application_event.event.PointChangeEvent;
import login.application_event.event.PointChangeReason;
import login.application_event.event.PointChangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointChangeEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Member member, PointChangeType changeType, int pointAmount, PointChangeReason reason, Long reasonId){
        PointChangeEvent event = new PointChangeEvent(member, changeType, pointAmount, reason, reasonId);
        publisher.publishEvent(event);
    }

}
