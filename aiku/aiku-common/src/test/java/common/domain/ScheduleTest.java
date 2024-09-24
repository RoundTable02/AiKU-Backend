package common.domain;

import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ScheduleTest {

    @Test
    void createWithNoPoint() {
        //given
        Member member = Member.create("member1");

        //when
        Schedule schedule = Schedule.create(member, null, "sch1", LocalDateTime.now(),
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

        //when
        Schedule schedule = Schedule.create(member, null, "sch1", LocalDateTime.now(),
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

        Schedule schedule = Schedule.create(member, null, "sch1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 100);

        //when
        String scheduleName = "new";
        Location location = new Location("new", 2.0, 2.0);
        schedule.update(scheduleName, LocalDateTime.now(), location);

        //then
        assertThat(schedule.getScheduleName()).isEqualTo(scheduleName);
        assertThat(schedule.getLocation()).isEqualTo(location);
    }

    @Test
    void addScheduleMember(){
        //given
        Member member = Member.create("member1");
        Member member2 = Member.create("member2");

        Schedule schedule = Schedule.create(member, null, "sch1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 0);

        //when
        schedule.addScheduleMember(member2, false, 100);

        //then

        List<ScheduleMember> scheduleMembers = schedule.getScheduleMembers();
        assertThat(scheduleMembers.size()).isEqualTo(2);
        assertThat(scheduleMembers).extracting("isOwner").contains(true, false);
        assertThat(scheduleMembers).extracting("isPaid").contains(true, false);
        assertThat(scheduleMembers).extracting("pointAmount").contains(0, 100);
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getNickname))
                .contains(member.getNickname(), member2.getNickname());
    }
}