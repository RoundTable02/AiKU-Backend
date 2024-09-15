package aiku_main.integration_test;

import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleEnterDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import aiku_main.service.ScheduleService;
import common.domain.ExecStatus;
import common.domain.Location;
import common.domain.ScheduleMember;
import common.domain.member.Member;
import common.domain.Schedule;
import common.domain.team.Team;
import common.exception.BaseException;
import common.exception.NoAuthorityException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class ScheduleServiceIntegrationTest {

    @Autowired
    EntityManager em;

    @InjectMocks
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleRepository scheduleRepository;

    Random random = new Random();

    @Test
    @DisplayName("스케줄 등록-권한O/X")
    void addSchedule() {
        //given
        Member member = Member.create("member1");
        Member noMember = Member.create("noMember");
        em.persist(member);
        em.persist(noMember);

        Team team = Team.create(member, "team1");
        em.persist(team);

        //when
        ScheduleAddDto scheduleDto = new ScheduleAddDto("sche1",
                new Location("lo1", 1.0, 1.0), LocalDateTime.now().plusHours(1), 0);
        Long scheduleId = scheduleService.addSchedule(member, team.getId(), scheduleDto);

        em.flush();
        em.clear();

        //then
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        assertThat(schedule).isNotNull();
        assertThat(schedule.getScheduleStatus()).isEqualTo(ExecStatus.WAIT);
        assertThat(schedule.getScheduleMembers().size()).isEqualTo(1);
        assertThat(schedule.getScheduleMembers().get(0).getMember().getId()).isEqualTo(member.getId());

        //권한x
        assertThatThrownBy(() -> scheduleService.addSchedule(noMember, team.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("스케줄 수정-권한O/X-")
    void updateSchedule() {
        //given
        Member member = Member.create("member1");
        Member member2 = Member.create("member2");
        em.persist(member);
        em.persist(member2);

        Team team = Team.create(member, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member, team.getId(), 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new Location("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        scheduleService.updateSchedule(member, schedule.getId(), scheduleDto);

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(findSchedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
        assertThat(findSchedule.getLocation().getLocationName()).isEqualTo(scheduleDto.getLocation().getLocationName());

        //권한 x
        assertThatThrownBy(() -> scheduleService.updateSchedule(member2, schedule.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("스케줄 입장-기본/중복 입장,권한O/X")
    void enterSchedule() {
        //given
        Member member = Member.create("member1");
        Member member2 = Member.create("member2");
        Member noMember = Member.create("noMember");
        em.persist(member);
        em.persist(member2);
        em.persist(noMember);

        Team team = Team.create(member, "team1");
        team.addTeamMember(member2, false);
        em.persist(team);

        Schedule schedule = createSchedule(member, team.getId(), 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        Long scheduleId = scheduleService.enterSchedule(member2, team.getId(), schedule.getId(), enterDto);

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(scheduleId).orElse(null);
        assertThat(findSchedule).isNotNull();

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers.size()).isEqualTo(2);
        assertThat(scheduleMembers).extracting("isOwner").contains(true, false);
        assertThat(scheduleMembers).extracting("isPaid").contains(true, false);
        assertThat(scheduleMembers).extracting("pointAmount").contains(100, 0);
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getId)).contains(member.getId(), member2.getId());

        //권한x-그룹에 속해 있지 않을 때
        assertThatThrownBy(() -> scheduleService.enterSchedule(noMember, team.getId(), schedule.getId(), enterDto)).isInstanceOf(NoAuthorityException.class);
        //중복 요청
        assertThatThrownBy(() -> scheduleService.enterSchedule(member, team.getId(), schedule.getId(), enterDto)).isInstanceOf(BaseException.class);
    }

    Schedule createSchedule(Member member, Long teamId, int pointAmount){
        return Schedule.create(member, teamId, UUID.randomUUID().toString(), LocalDateTime.now(),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()), pointAmount);
    }
}
