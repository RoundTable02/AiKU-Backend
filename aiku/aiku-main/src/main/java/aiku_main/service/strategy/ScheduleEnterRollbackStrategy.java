package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.ScheduleQueryRepository;
import aiku_main.scheduler.ScheduleScheduler;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PointChangeReasonMapping(PointChangeReason.SCHEDULE_ENTER)
@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleEnterRollbackStrategy implements RollbackStrategy {

    private final ScheduleQueryRepository scheduleRepository;
    private final ScheduleScheduler scheduleScheduler;

    @Override
    public void execute(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        Schedule schedule = scheduleRepository.findById(reasonId).orElseThrow();
        ScheduleMember scheduleMember = scheduleRepository.findScheduleMember(memberId, schedule.getId()).orElseThrow();

        schedule.errorScheduleMember(scheduleMember);

        if (scheduleMember.isOwner()) {
            schedule.error();
            scheduleScheduler.cancelAll(schedule.getId());
        }
    }
}
