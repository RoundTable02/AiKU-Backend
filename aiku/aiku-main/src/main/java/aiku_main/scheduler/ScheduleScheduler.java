package aiku_main.scheduler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleOpenEvent;
import aiku_main.repository.schedule.ScheduleRepository;
import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@RequiredArgsConstructor
@Component
public class ScheduleScheduler {

    private final TaskScheduler scheduler;
    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final ConcurrentHashMap<Long, ScheduledFuture> scheduleOpenTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture> scheduleAutoCloseTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture> scheduleAlarmTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void initScheduler(){
        List<Schedule> schedules = scheduleRepository.findByScheduleStatus(ExecStatus.WAIT);
        schedules.forEach(schedule -> reserveSchedule(schedule));
    }

    public void reserveSchedule(Schedule schedule){
        reserveScheduleOpen(schedule);
        reserveScheduleAutoClose(schedule);
        reserveAlarm(schedule);
    }

    public void changeSchedule(Schedule schedule){
        cancelAll(schedule.getId());
        reserveSchedule(schedule);
    }

    //TODO Runnable 추가
    private void reserveAlarm(Schedule schedule){
        Duration delayTime = getDuration(schedule.getScheduleTime());
        if(!isValidDuration(delayTime)) return;

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(()->{}, delayTime);
        scheduleAlarmTasks.put(schedule.getId(), future);
    }

    private void reserveScheduleOpen(Schedule schedule){
        Duration delayTime = getDuration(schedule.getScheduleTime()).minus(Duration.ofMinutes(30));
        if(!isValidDuration(delayTime)) return;

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                ()-> publishScheduleOpenEvent(schedule.getId()),
                delayTime
        );
        scheduleOpenTasks.put(schedule.getId(), future);
    }

    private void reserveScheduleAutoClose(Schedule schedule){
        Duration delayTime = getDuration(schedule.getScheduleTime()).plus(Duration.ofMinutes(30));
        if(!isValidDuration(delayTime)) return;

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                ()-> publishScheduleAutoCloseEvent(schedule.getId()),
                delayTime
        );
        scheduleAutoCloseTasks.put(schedule.getId(), future);
    }

    public void cancelAll(Long scheduleId){
        ScheduledFuture future1 = scheduleOpenTasks.get(scheduleId);
        if (future1 != null) {
            future1.cancel(false);
            scheduleOpenTasks.remove(scheduleId);
        }

        ScheduledFuture future2 = scheduleAutoCloseTasks.get(scheduleId);
        if (future2 != null) {
            future2.cancel(false);
            scheduleAutoCloseTasks.remove(scheduleId);
        }

        ScheduledFuture future3 = scheduleAlarmTasks.get(scheduleId);
        if (future3 != null) {
            future3.cancel(false);
            scheduleAlarmTasks.remove(scheduleId);
        }
    }

    private Duration getDuration(LocalDateTime time){
        return Duration.between(LocalDateTime.now(), time);
    }

    private boolean isValidDuration(Duration duration){
        if(duration.toMinutes() < 0) return false;
        else return true;
    }

    private void publishScheduleOpenEvent(Long scheduleId){
        ScheduleOpenEvent event = new ScheduleOpenEvent(scheduleId);
        eventPublisher.publishEvent(event);
    }

    private void publishScheduleAutoCloseEvent(Long scheduleId){
        ScheduleAutoCloseEvent event = new ScheduleAutoCloseEvent(scheduleId);
        eventPublisher.publishEvent(event);
    }
}
