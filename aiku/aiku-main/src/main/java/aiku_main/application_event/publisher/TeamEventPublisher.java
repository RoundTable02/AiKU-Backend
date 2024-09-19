package aiku_main.application_event.publisher;

import aiku_main.application_event.event.TeamExitEvent;
import common.domain.member.Member;
import common.domain.team.Team;
import common.domain.value_reference.MemberValue;
import common.domain.value_reference.TeamValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Member member, Team team){
        TeamExitEvent event = new TeamExitEvent(member, team);
        publisher.publishEvent(event);
    }

}
