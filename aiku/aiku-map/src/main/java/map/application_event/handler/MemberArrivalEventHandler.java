package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.MemberArrivalEvent;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberArrivalEventHandler {

    private final RacingService racingService;

    @EventListener
    public void makeMemberWinnerInRacing(MemberArrivalEvent event){
        racingService.makeMemberWinnerInRacing(event.getMemberId(), event.getScheduleId(), event.getScheduleName());
    }
}
