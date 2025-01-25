package aiku_main.application_event.publisher;

import aiku_main.application_event.event.TeamExitEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishTeamExitEvent(Long memberId, Long teamId){
        TeamExitEvent event = new TeamExitEvent(memberId, teamId);
        publisher.publishEvent(event);
    }
}
