package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.repository.ScheduleQueryRepository;
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
class ScheduleRollbackStrategyTest {

    @Autowired
    EntityManager em;
    @Autowired
    ScheduleQueryRepository scheduleRepository;
    @Autowired
    ScheduleRollbackStrategy scheduleRollbackStrategy;

    Member member;
    Schedule schedule;

    @BeforeEach
    void beforeEach() {
        member = Member.builder()
                .nickname("mem")
                .build();

        schedule = Schedule.create(
                member,
                null,
                "sche",
                LocalDateTime.now(),
                null,
                10
        );

        em.persist(member);
        em.persist(schedule);
    }

    @Test
    void execute() {
        //when
        scheduleRollbackStrategy.execute(
                member.getId(),
                PointChangeType.PLUS,
                10,
                PointChangeReason.SCHEDULE_REWARD,
                schedule.getId()
        );

        //then
        assertThat(schedule.getScheduleMembers())
                .extracting(ScheduleMember::getStatus)
                .containsExactly(ERROR);
        assertThat(schedule.getStatus()).isEqualTo(ALIVE);
    }
}