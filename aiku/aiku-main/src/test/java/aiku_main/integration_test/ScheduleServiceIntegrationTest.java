package aiku_main.integration_test;

import aiku_main.dto.*;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleRepository;
import aiku_main.service.ScheduleService;
import common.domain.*;
import common.domain.member.Member;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import common.exception.BaseException;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
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
    void 스케줄_등록() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        ScheduleAddDto scheduleDto = new ScheduleAddDto("sche1",
                new LocationDto("lo1", 1.0, 1.0), LocalDateTime.now().plusHours(1), 0);
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
    void 스케줄_등록_팀멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        ScheduleAddDto scheduleDto = new ScheduleAddDto("sche1",
                new LocationDto("lo1", 1.0, 1.0), LocalDateTime.now().plusHours(1), 0);
        assertThatThrownBy(() -> scheduleService.addSchedule(member2, team.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 스케줄_수정() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        scheduleService.updateSchedule(member1, schedule.getId(), scheduleDto);

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(findSchedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
        assertThat(findSchedule.getLocation().getLocationName()).isEqualTo(scheduleDto.getLocation().getLocationName());
    }

    @Test
    void 스케줄_수정_방장x() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 0);
        schedule.addScheduleMember(member2, false, 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        assertThatThrownBy(() -> scheduleService.updateSchedule(member2, schedule.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 스케줄_수정_스케줄시간1시간이내() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = Schedule.create(member1, null, "sche1", LocalDateTime.now().plusMinutes(30),
                new Location("loc1", 1.0, 1.0), 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        assertThatThrownBy(() -> scheduleService.updateSchedule(member1, schedule.getId(), scheduleDto)).isInstanceOf(BaseExceptionImpl.class);
    }

    @Test
    void 스케줄_입장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
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
    }

    @Test
    void 스케줄_입장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        schedule.addScheduleMember(member2, false, 0);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2, team.getId(), schedule.getId(), enterDto)).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_입장_팀멤버X() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2, team.getId(), schedule.getId(), enterDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 스케줄_입장_대기스케줄x() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        schedule.setScheduleStatus(ExecStatus.RUN);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2, team.getId(), schedule.getId(), enterDto)).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_퇴장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
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
        assertThat(findSchedule.getStatus()).isEqualTo(ALIVE);

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers.size()).isEqualTo(3);
        assertThat(scheduleMembers).extracting("status").contains(ALIVE, ALIVE, DELETE);
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getId)).contains(member1.getId(), member2.getId());

        ScheduleMember owner = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule.getId()).orElse(null);
        assertThat(owner).isNotNull();
        assertThat(owner.isOwner()).isTrue();
        //중복 요청
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_퇴장_스케줄멤버X() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_퇴장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        schedule.addScheduleMember(member2, false, 0);
        em.persist(schedule);

        scheduleService.exitSchedule(member2, team.getId(), schedule.getId());
        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_퇴장_남은멤버x_자동삭제() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        scheduleService.exitSchedule(member1, team.getId(), schedule.getId());

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getStatus()).isEqualTo(DELETE);

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers).extracting("status").contains(DELETE);
    }

    @Test
    void 스케줄_퇴장_방장퇴장_방장변경() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 0);
        schedule.addScheduleMember(member2, false, 0);

        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        scheduleService.exitSchedule(member1, team.getId(), schedule.getId());

        em.flush();
        em.clear();

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getStatus()).isEqualTo(ALIVE);

        ScheduleMember nextOwner = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule.getId()).orElse(null);
        assertThat(nextOwner).isNotNull();
        assertThat(nextOwner.isOwner()).isTrue();
    }

    @Test
    void 스케줄_퇴장_대기스케줄x() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 0);
        schedule.setScheduleStatus(ExecStatus.TERM);
        schedule.addScheduleMember(member2, false, 0);

        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member1, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    void 스케줄_상세_조회() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        team.addTeamMember(member4);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
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
    }

    @Test
    void 스케줄_상세_조회_스케줄멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        team.addTeamMember(member4);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        schedule.addScheduleMember(member2, false, 0);
        schedule.addScheduleMember(member3, false, 100);
        em.persist(schedule);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> scheduleService.getScheduleDetail(member4, team.getId(), schedule.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 그룹_스케줄_목록_조회() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 100);
        schedule1.setScheduleStatus(ExecStatus.RUN);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member2, team, 100);
        schedule2.addScheduleMember(member3, false, 100);
        schedule2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule2);

        Schedule schedule3 = createSchedule(member3, team, 100);
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
    }

    @Test
    void 그룹_스케줄_목록_조회_스케줄멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        assertThatThrownBy(() -> scheduleService.getTeamScheduleList(member4, team.getId(), new SearchDateCond(), 1)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 그룹_스케줄_목록_조회_필터링() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.setScheduleStatus(ExecStatus.RUN);
        em.persist(schedule1);

        LocalDateTime startDate = LocalDateTime.now().plusHours(3);

        Schedule schedule2 = createSchedule(member2, team, 100);
        schedule2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(schedule2);

        LocalDateTime endDate = LocalDateTime.now().plusHours(3);

        Schedule schedule3 = createSchedule(member3, team, 100);
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

    @Test
    void 멤버_스케줄_목록_조회() {
        //given
        Team teamA = Team.create(member1, "teamA");
        teamA.addTeamMember(member2);
        teamA.addTeamMember(member3);
        em.persist(teamA);

        Team teamB = Team.create(member1, "teamB");
        teamB.addTeamMember(member2);
        em.persist(teamB);

        Schedule scheduleA1 = createSchedule(member1, teamA, 100);
        scheduleA1.setScheduleStatus(ExecStatus.RUN);
        scheduleA1.addScheduleMember(member2, false, 0);
        em.persist(scheduleA1);

        Schedule scheduleA2 = createSchedule(member2, teamA, 100);
        scheduleA2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(scheduleA2);

        Schedule scheduleB1 = createSchedule(member1, teamB, 0);
        scheduleB1.setScheduleStatus(ExecStatus.RUN);
        em.persist(scheduleB1);

        em.flush();
        em.clear();

        //when
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(member1, new SearchDateCond(), 1);

        //then
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getWaitSchedule()).isEqualTo(0);
        assertThat(result.getRunSchedule()).isEqualTo(2);

        List<MemberScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("groupId").containsExactly(teamB.getId(), teamA.getId());
        assertThat(schedules).extracting("scheduleId").containsExactly(scheduleB1.getId(), scheduleA1.getId());
        assertThat(schedules).extracting("memberSize").containsExactly(1, 2);
    }

    @Test
    void 멤버_스케줄_목록_조회_필터링() {
        //given
        Team teamA = Team.create(member1, "teamA");
        teamA.addTeamMember(member2);
        teamA.addTeamMember(member3);
        em.persist(teamA);

        Team teamB = Team.create(member1, "teamB");
        teamB.addTeamMember(member2);
        em.persist(teamB);

        Schedule scheduleA1 = createSchedule(member1, teamA, 100);
        scheduleA1.setScheduleStatus(ExecStatus.RUN);
        scheduleA1.addScheduleMember(member2, false, 0);
        em.persist(scheduleA1);

        Schedule scheduleA2 = createSchedule(member2, teamA, 100);
        scheduleA2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(scheduleA2);

        LocalDateTime startDate = LocalDateTime.now().plusHours(3);

        Schedule scheduleB1 = createSchedule(member1, teamB, 0);
        scheduleB1.setScheduleStatus(ExecStatus.RUN);
        em.persist(scheduleB1);

        Schedule scheduleB2 = createSchedule(member1, teamB, 0);
        scheduleB2.setScheduleStatus(ExecStatus.WAIT);
        em.persist(scheduleB2);

        em.flush();
        em.clear();

        //when
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(member1, new SearchDateCond(startDate, null), 1);

        //then
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getWaitSchedule()).isEqualTo(1);
        assertThat(result.getRunSchedule()).isEqualTo(1);

        List<MemberScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("groupId").containsExactly(teamB.getId(), teamB.getId());
        assertThat(schedules).extracting("scheduleId").containsExactly(scheduleB2.getId(), scheduleB1.getId());
        assertThat(schedules).extracting("memberSize").containsExactly(1, 1);
    }

    Schedule createSchedule(Member member, Team team, int pointAmount){
        return Schedule.create(member, new TeamValue(team), UUID.randomUUID().toString(), LocalDateTime.now().plusHours(3),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()), pointAmount);
    }
}
