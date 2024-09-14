package aiku_main.application_event.publisher;

import aiku_main.application_event.event.TeamExitEvent;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.TeamValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Long memberId, Long teamId){
        TeamExitEvent event = new TeamExitEvent(new MemberValue(memberId), new TeamValue(teamId));
        publisher.publishEvent(event);
    }

}
