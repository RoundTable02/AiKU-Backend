package aiku_main.application_event.publisher;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointChangeEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Long memberId, PointChangeType changeType, int pointAmount, PointChangeReason reason, Long reasonId){
        PointChangeEvent event = new PointChangeEvent(memberId, changeType, pointAmount, reason, reasonId);
        publisher.publishEvent(event);
    }

    public void consumerPublish(Long memberId, PointChangeType changeType, int pointAmount, PointChangeReason reason, Long reasonId){
        PointChangeEvent event = new PointChangeEvent(memberId, changeType, pointAmount, reason, reasonId);
        publisher.publishEvent(event);
    }

}
