package common.domain;

import common.domain.member.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleTest {

    @Test
    void createWithNoPoint() {
        //given
        Member member = Member.create("member1");
        Long memberId = getRandomId();

        Long teamId = getRandomId();

        //when
        Schedule schedule = Schedule.create(member, teamId, "sch1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 0);

        //then
        assertThat(schedule.getScheduleMembers().size()).isEqualTo(1);

        ScheduleMember scheduleMember = schedule.getScheduleMembers().get(0);
        assertThat(scheduleMember.getSchedule()).isEqualTo(schedule);
        assertThat(scheduleMember.getMember()).isEqualTo(member);
        assertThat(scheduleMember.isOwner()).isEqualTo(true);
        assertThat(scheduleMember.isPaid()).isEqualTo(false);
    }

    @Test
    void createWithPoint() {
        //given
        Member member = Member.create("member1");
        Long teamId = getRandomId();

        //when
        Schedule schedule = Schedule.create(member, teamId, "sch1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 100);

        //then
        assertThat(schedule.getScheduleMembers().size()).isEqualTo(1);

        ScheduleMember scheduleMember = schedule.getScheduleMembers().get(0);
        assertThat(scheduleMember.getSchedule()).isEqualTo(schedule);
        assertThat(scheduleMember.getMember()).isEqualTo(member);
        assertThat(scheduleMember.isOwner()).isEqualTo(true);
        assertThat(scheduleMember.isPaid()).isEqualTo(true);
        assertThat(scheduleMember.getPointAmount()).isEqualTo(100);
    }

    @Test
    void update() {
        //given
        Member member = Member.create("member1");
        Long teamId = getRandomId();

        Schedule schedule = Schedule.create(member, teamId, "sch1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 100);

        //when
        String scheduleName = "new";
        Location location = new Location("new", 2.0, 2.0);
        schedule.update(scheduleName, LocalDateTime.now(), location);

        //then
        assertThat(schedule.getScheduleName()).isEqualTo(scheduleName);
        assertThat(schedule.getLocation()).isEqualTo(location);
    }

    Long getRandomId(){
        Random random = new Random();
        return random.nextLong();
    }
}