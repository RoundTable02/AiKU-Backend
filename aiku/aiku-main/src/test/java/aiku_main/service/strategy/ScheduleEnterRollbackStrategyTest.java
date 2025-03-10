package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static common.domain.Status.ALIVE;
import static common.domain.Status.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ScheduleEnterRollbackStrategyTest {

    @Autowired
    EntityManager em;
    @Autowired
    ScheduleEnterRollbackStrategy scheduleEnterRollbackStrategy;

    Member scheduleOwner;
    Member member;
    Schedule schedule;

    @BeforeEach
    void beforeEach() {
        scheduleOwner = Member.builder()
                .nickname("owner")
                .build();

        member = Member.builder()
                .nickname("mem")
                .build();

        schedule = Schedule.create(
                scheduleOwner,
                null,
                "sche",
                LocalDateTime.now(),
                null,
                10
        );

        em.persist(scheduleOwner);
        em.persist(member);
        em.persist(schedule);
    }

    @Test
    void 스케줄_롤백_스케줄장() {
        //when
        scheduleEnterRollbackStrategy.execute(
                scheduleOwner.getId(),
                PointChangeType.MINUS,
                10,
                PointChangeReason.SCHEDULE_ENTER,
                schedule.getId()
        );

        //then
        assertThat(schedule.getStatus()).isEqualTo(ERROR);
    }

    @Test
    void 스케줄_롤백() {
        //given
        schedule.addScheduleMember(member, false, 10);
        em.flush();

        //when
        scheduleEnterRollbackStrategy.execute(
                member.getId(),
                PointChangeType.MINUS,
                10,
                PointChangeReason.SCHEDULE_ENTER,
                schedule.getId()
        );

        //then
        assertThat(schedule.getStatus()).isEqualTo(ALIVE);
        assertThat(schedule.getScheduleMembers())
                .extracting(ScheduleMember::getStatus)
                .containsExactlyInAnyOrder(ALIVE, ERROR);
    }

}