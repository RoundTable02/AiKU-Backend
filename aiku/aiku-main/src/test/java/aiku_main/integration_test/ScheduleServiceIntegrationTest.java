package aiku_main.integration_test;

import aiku_main.dto.*;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleRepository;
import aiku_main.service.ScheduleService;
import common.domain.*;
import common.domain.member.Member;
import common.domain.team.Team;
import common.exception.BaseException;
import common.exception.NoAuthorityException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static common.domain.Status.ALIVE;
import static common.domain.Status.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class ScheduleServiceIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    MemberRepository memberRepository;

    Member member1;
    Member member2;
    Member member3;
    Member member4;

    Random random = new Random();

    @BeforeEach
    void beforeEach(){
        member1 = Member.create("member1");
        member2 = Member.create("member2");
        member3 = Member.create("member3");
        member4 = Member.create("member4");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("스케줄 등록-권한O/X")
    void addSchedule() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        ScheduleAddDto scheduleDto = new ScheduleAddDto("sche1",
                new Location("lo1", 1.0, 1.0), LocalDateTime.now().plusHours(1), 0);
        Long scheduleId = scheduleService.addSchedule(member1, team.getId(), scheduleDto);

        em.flush();
        em.clear();

        //then
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        assertThat(schedule).isNotNull();
        assertThat(schedule.getScheduleStatus()).isEqualTo(ExecStatus.WAIT);
        assertThat(schedule.getScheduleMembers().size()).isEqualTo(1);
        assertThat(schedule.getScheduleMembers().get(0).getMember().getId()).isEqualTo(member1.getId());

        //권한x
        assertThatThrownBy(() -> scheduleService.addSchedule(member3, team.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("스케줄 수정-권한O/X-")
    void updateSchedule() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team.getId(), 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new Location("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        scheduleService.updateSchedule(member1, schedule.getId(), scheduleDto);

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
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2, false);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team.getId(), 100);
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
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getId)).contains(member1.getId(), member2.getId());

        //권한x-그룹에 속해 있지 않을 때
        assertThatThrownBy(() -> scheduleService.enterSchedule(member3, team.getId(), schedule.getId(), enterDto)).isInstanceOf(NoAuthorityException.class);
        //중복 요청
        assertThatThrownBy(() -> scheduleService.enterSchedule(member1, team.getId(), schedule.getId(), enterDto)).isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("스케줄 퇴장-기본/중복 퇴장,권한O/X")
    void exitSchedule() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2, false);
        team.addTeamMember(member3, false);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team.getId(), 100);
        schedule.addScheduleMember(member2, false, 0);
        schedule.addScheduleMember(member3, false, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        scheduleService.exitSchedule(member2, team.getId(), schedule.getId());

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers.size()).isEqualTo(3);
        assertThat(scheduleMembers).extracting("status").contains(ALIVE, ALIVE, DELETE);
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getId)).contains(member1.getId(), member2.getId());

        //권한x-그룹에 속해 있지 않을 때
        assertThatThrownBy(() -> scheduleService.exitSchedule(member4, team.getId(), schedule.getId())).isInstanceOf(NoAuthorityException.class);
        //중복 요청
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2, team.getId(), schedule.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("스케줄 상세 조회-권한O/X")
    void getScheduleDetail() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2, false);
        team.addTeamMember(member3, false);
        team.addTeamMember(member4, false);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team.getId(), 100);
        schedule.addScheduleMember(member2, false, 0);
        schedule.addScheduleMember(member3, false, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleDetailResDto resultDto = scheduleService.getScheduleDetail(member1, team.getId(), schedule.getId());

        em.flush();
        em.clear();

        //then
        assertThat(resultDto.getScheduleId()).isEqualTo(schedule.getId());

        List<ScheduleMemberResDto> scheduleMembers = resultDto.getMembers();
        assertThat(scheduleMembers.size()).isEqualTo(3);
        assertThat(scheduleMembers).extracting("memberId").containsExactly(member1.getId(), member2.getId(), member3.getId());

        //권한 x-스케줄 멤버가 아닐때
        assertThatThrownBy(() -> scheduleService.getScheduleDetail(member4, team.getId(), schedule.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("그룹의 스케줄 목록 조회-필터링x,권한O/X")
    void getTeamScheduleList() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team.getId(), 100);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 100);
        schedule1.setScheduleStatus(ExecStatus.RUN);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member2, team.getId(), 100);
        schedule2.addScheduleMember(member3, false, 100);
        schedule2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule2);

        Schedule schedule3 = createSchedule(member3, team.getId(), 100);
        schedule3.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule3);

        em.flush();
        em.clear();

        //when
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(member1, team.getId(), new SearchDateCond(), 1);

        em.flush();
        em.clear();

        //then
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getRunSchedule()).isEqualTo(1);
        assertThat(result.getWaitSchedule()).isEqualTo(2);

        List<TeamScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("scheduleId").containsExactly(schedule3.getId(), schedule2.getId(), schedule1.getId());
        assertThat(schedules).extracting("memberSize").containsExactly(1, 2, 3);
        assertThat(schedules).extracting("accept").containsExactly(false, false, true);

        //권한 x
        assertThatThrownBy(() -> scheduleService.getTeamScheduleList(member4, team.getId(), new SearchDateCond(), 1)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("그룹의 스케줄 목록 조회-필터링o")
    void getTeamScheduleListWithFilter() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team.getId(), 100);
        schedule1.setScheduleStatus(ExecStatus.RUN);
        em.persist(schedule1);

        LocalDateTime startDate = LocalDateTime.now();

        Schedule schedule2 = createSchedule(member2, team.getId(), 100);
        schedule2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule2);

        LocalDateTime endDate = LocalDateTime.now();

        Schedule schedule3 = createSchedule(member3, team.getId(), 100);
        schedule3.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule3);

        em.flush();
        em.clear();

        //when
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(member1, team.getId(), new SearchDateCond(startDate, endDate), 1);

        em.flush();
        em.clear();

        //then
        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getRunSchedule()).isEqualTo(0);
        assertThat(result.getWaitSchedule()).isEqualTo(1);

        List<TeamScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("scheduleId").containsExactly(schedule2.getId());
        assertThat(schedules).extracting("accept").containsExactly(false);
    }

    Schedule createSchedule(Member member, Long teamId, int pointAmount){
        return Schedule.create(member, teamId, UUID.randomUUID().toString(), LocalDateTime.now(),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()), pointAmount);
    }
}
