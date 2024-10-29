package aiku_main.integration_test;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.application_event.domain.ScheduleArrivalResult;
import aiku_main.dto.*;
import aiku_main.exception.ScheduleException;
import aiku_main.exception.TeamException;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleQueryRepository;
import aiku_main.service.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.*;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceIntegrationTest.class);
    @Autowired
    EntityManager em;
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    ScheduleQueryRepository scheduleQueryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;

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
        Long scheduleId = scheduleService.addSchedule(member1.getId(), team.getId(), scheduleDto);

        //then
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElse(null);
        assertThat(schedule).isNotNull();
        assertThat(schedule.getScheduleStatus()).isEqualTo(ExecStatus.WAIT);
        assertThat(schedule.getScheduleMembers().size()).isEqualTo(1);
        assertThat(schedule.getScheduleMembers().get(0).getMember().getId()).isEqualTo(member1.getId());
    }

    @Test
    void 스케줄_등록_팀멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        ScheduleAddDto scheduleDto = new ScheduleAddDto("sche1",
                new LocationDto("lo1", 1.0, 1.0), LocalDateTime.now().plusHours(1), 0);
        assertThatThrownBy(() -> scheduleService.addSchedule(member2.getId(), team.getId(), scheduleDto)).isInstanceOf(TeamException.class);
    }

    @Test
    void 스케줄_수정() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 0);
        em.persist(schedule);

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        scheduleService.updateSchedule(member1.getId(), schedule.getId(), scheduleDto);

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule.getId()).get();
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

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        assertThatThrownBy(() -> scheduleService.updateSchedule(member2.getId(), schedule.getId(), scheduleDto)).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 스케줄_수정_스케줄시간1시간이내() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = Schedule.create(member1, null, "sche1", LocalDateTime.now().plusMinutes(30),
                new Location("loc1", 1.0, 1.0), 0);
        em.persist(schedule);

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new LocationDto("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        assertThatThrownBy(() -> scheduleService.updateSchedule(member1.getId(), schedule.getId(), scheduleDto)).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 스케줄_입장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        Long scheduleId = scheduleService.enterSchedule(member2.getId(), team.getId(), schedule.getId(), enterDto);

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(scheduleId).orElse(null);
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

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2.getId(), team.getId(), schedule.getId(), enterDto)).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 스케줄_입장_팀멤버X() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2.getId(), team.getId(), schedule.getId(), enterDto)).isInstanceOf(TeamException.class);
    }

    @Test
    void 스케줄_입장_대기스케줄x() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        schedule.setRun();
        em.persist(schedule);

        //when
        ScheduleEnterDto enterDto = new ScheduleEnterDto(0);
        assertThatThrownBy(() -> scheduleService.enterSchedule(member2.getId(), team.getId(), schedule.getId(), enterDto)).isInstanceOf(ScheduleException.class);
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

        //when
        scheduleService.exitSchedule(member2.getId(), team.getId(), schedule.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getStatus()).isEqualTo(ALIVE);

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers.size()).isEqualTo(3);
        assertThat(scheduleMembers).extracting("status").contains(ALIVE, ALIVE, DELETE);
        assertThat(scheduleMembers.stream().map(ScheduleMember::getMember).map(Member::getId)).contains(member1.getId(), member2.getId());

        ScheduleMember owner = scheduleQueryRepository.findScheduleMember(member1.getId(), schedule.getId()).orElse(null);
        assertThat(owner).isNotNull();
        assertThat(owner.isOwner()).isTrue();
    }

    @Test
    void 스케줄_퇴장_스케줄멤버X() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2.getId(), team.getId(), schedule.getId())).isInstanceOf(ScheduleException.class);
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

        scheduleService.exitSchedule(member2.getId(), team.getId(), schedule.getId());

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2.getId(), team.getId(), schedule.getId())).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 스케줄_퇴장_남은멤버x_자동삭제() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        scheduleService.exitSchedule(member1.getId(), team.getId(), schedule.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule.getId()).orElse(null);
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

        //when
        scheduleService.exitSchedule(member1.getId(), team.getId(), schedule.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getStatus()).isEqualTo(ALIVE);

        ScheduleMember nextOwner = scheduleQueryRepository.findScheduleMember(member2.getId(), schedule.getId()).orElse(null);
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
        schedule.setTerm(LocalDateTime.now());
        schedule.addScheduleMember(member2, false, 0);
        em.persist(schedule);

        //when
        assertThatThrownBy(() -> scheduleService.exitSchedule(member1.getId(), team.getId(), schedule.getId())).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 스케줄_종료(){
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        schedule.close(LocalDateTime.now());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getScheduleStatus()).isEqualTo(ExecStatus.TERM);
    }

    @Test
    void 스케줄_도착() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member1, team, 100);
        em.persist(schedule);

        //when
        LocalDateTime arrivalTime = LocalDateTime.now();
        scheduleService.arriveSchedule(schedule.getId(), member1.getId(), arrivalTime);

        //then
        ScheduleMember scheduleMember = scheduleQueryRepository.findScheduleMember(member1.getId(), schedule.getId()).orElse(null);
        assertThat(scheduleMember).isNotNull();
        assertThat(scheduleMember.getArrivalTimeDiff()).isEqualTo(Duration.between(arrivalTime, schedule.getScheduleTime()).toMinutes());
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

        //when
        ScheduleDetailResDto resultDto = scheduleService.getScheduleDetail(member1.getId(), team.getId(), schedule.getId());

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

        //when
        assertThatThrownBy(() -> scheduleService.getScheduleDetail(member4.getId(), team.getId(), schedule.getId())).isInstanceOf(ScheduleException.class);
    }

    @Test
    void 그룹_스케줄_목록_조회() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 100);
        schedule1.setRun();
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member2, team, 100);
        schedule2.addScheduleMember(member3, false, 100);
        em.persist(schedule2);

        Schedule schedule3 = createSchedule(member3, team, 100);
        em.persist(schedule3);

        //when
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(member1.getId(), team.getId(), new SearchDateCond(), 1);

        //then
        assertThat(result.getRunSchedule()).isEqualTo(1);
        assertThat(result.getWaitSchedule()).isEqualTo(2);

        List<TeamScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("scheduleId").containsExactly(schedule3.getId(), schedule2.getId(), schedule1.getId());
        assertThat(schedules).extracting("memberSize").containsExactly(1, 2, 3);
        assertThat(schedules).extracting("accept").containsExactly(false, false, true);
    }

    @Test
    void 그룹_스케줄_목록_조회_그룹멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        assertThatThrownBy(() -> scheduleService.getTeamScheduleList(member4.getId(), team.getId(), new SearchDateCond(), 1)).isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_스케줄_목록_조회_필터링() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.setRun();
        em.persist(schedule1);

        LocalDateTime startDate = LocalDateTime.now().plusHours(3);

        Schedule schedule2 = createSchedule(member2, team, 100);
        em.persist(schedule2);

        LocalDateTime endDate = LocalDateTime.now().plusHours(3);

        Schedule schedule3 = createSchedule(member3, team, 100);
        em.persist(schedule3);

        //when
        TeamScheduleListResDto result = scheduleService.getTeamScheduleList(member1.getId(), team.getId(), new SearchDateCond(startDate, endDate), 1);

        //then
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
        scheduleA1.setRun();
        scheduleA1.addScheduleMember(member2, false, 0);
        em.persist(scheduleA1);

        Schedule scheduleA2 = createSchedule(member2, teamA, 100);
        em.persist(scheduleA2);

        Schedule scheduleB1 = createSchedule(member1, teamB, 0);
        scheduleB1.setRun();
        em.persist(scheduleB1);

        //when
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(member1.getId(), new SearchDateCond(), 1);

        //then
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
        scheduleA1.setRun();
        scheduleA1.addScheduleMember(member2, false, 0);
        em.persist(scheduleA1);

        Schedule scheduleA2 = createSchedule(member2, teamA, 100);
        em.persist(scheduleA2);

        LocalDateTime startDate = LocalDateTime.now().plusHours(3);

        Schedule scheduleB1 = createSchedule(member1, teamB, 0);
        scheduleB1.setRun();
        em.persist(scheduleB1);

        Schedule scheduleB2 = createSchedule(member1, teamB, 0);
        em.persist(scheduleB2);

        //when
        MemberScheduleListResDto result = scheduleService.getMemberScheduleList(member1.getId(), new SearchDateCond(startDate, null), 1);

        //then
        assertThat(result.getWaitSchedule()).isEqualTo(1);
        assertThat(result.getRunSchedule()).isEqualTo(1);

        List<MemberScheduleListEachResDto> schedules = result.getData();
        assertThat(schedules).extracting("groupId").containsExactly(teamB.getId(), teamB.getId());
        assertThat(schedules).extracting("scheduleId").containsExactly(scheduleB2.getId(), scheduleB1.getId());
        assertThat(schedules).extracting("memberSize").containsExactly(1, 1);
    }

    @Test
    void 이벤트핸들러_팀퇴장_참여중인_대기스케줄_퇴장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 0);
        schedule1.addScheduleMember(member2, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member1, team, 0);
        schedule2.addScheduleMember(member2, false, 0);
        em.persist(schedule2);

        Schedule schedule3 = createSchedule(member1, team, 0);
        schedule3.addScheduleMember(member2, false, 0);
        schedule3.setTerm(LocalDateTime.now());
        em.persist(schedule3);

        Team teamB = Team.create(member1, "teamB");
        em.persist(teamB);

        Schedule scheduleB1 = createSchedule(member1, teamB, 100);
        em.persist(scheduleB1);

        //when
        scheduleService.exitAllScheduleInTeam(member1.getId(), team.getId());

        //then
        assertThat(scheduleQueryRepository.existScheduleMember(member1.getId(), schedule1.getId())).isFalse();
        assertThat(scheduleQueryRepository.existScheduleMember(member1.getId(), schedule2.getId())).isFalse();
        assertThat(scheduleQueryRepository.existScheduleMember(member1.getId(), schedule3.getId())).isTrue();
        assertThat(scheduleQueryRepository.existScheduleMember(member1.getId(), scheduleB1.getId())).isTrue();
    }

    @Test
    void 이벤트핸들러_스케줄_자동종료() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1,  team, 0);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime);
        em.flush();
        em.clear();
        //when
        log.info("시작");
        scheduleService.closeScheduleAuto(schedule1.getId());
        log.info("끝");

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getScheduleStatus()).isEqualTo(ExecStatus.TERM);

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();
        assertThat(scheduleMembers).extracting("arrivalTimeDiff")
                .contains(-30, -30, (int) Duration.between(arrivalTime, schedule1.getScheduleTime()).toMinutes());
    }

    @Test
    void 이벤트핸들러_스케줄_자동종료_이미종료된스케줄() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 0);
        schedule1.addScheduleMember(member2, false, 0);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime);
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleService.closeScheduleAuto(schedule1.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);

        List<ScheduleMember> scheduleMembers = findSchedule.getScheduleMembers();

        int timeDiff = (int) Duration.between(arrivalTime, schedule1.getScheduleTime()).toMinutes();
        assertThat(scheduleMembers).extracting("arrivalTimeDiff").contains(timeDiff, timeDiff);
    }

    @Test
    void 이벤트핸들러_스케줄_결과_정산_지각자x(){
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.addScheduleMember(member2, false, 200);
        schedule1.addScheduleMember(member3, false, 300);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), arrivalTime);
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleService.processScheduleResultPoint(schedule1.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();

        assertThat(findSchedule.getScheduleMembers()).extracting("rewardPointAmount").contains(100, 200, 300);
    }

    @Test
    void 이벤트핸들러_스케줄_결과_정산_모두지각자(){
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.addScheduleMember(member2, false, 200);
        schedule1.addScheduleMember(member3, false, 300);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(4);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), arrivalTime);
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleService.processScheduleResultPoint(schedule1.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();

        assertThat(findSchedule.getScheduleMembers()).extracting("rewardPointAmount").contains(100, 200, 300);
    }

    @Test
    void 이벤트핸들러_스케줄_결과_정산(){
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 100);
        schedule1.addScheduleMember(member2, false, 200);
        schedule1.addScheduleMember(member3, false, 300);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime.plusHours(5));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), arrivalTime);
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleService.processScheduleResultPoint(schedule1.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();

        assertThat(findSchedule.getScheduleMembers()).extracting("rewardPointAmount").contains(0, 250, 350);
    }

    @Test
    void 이벤트핸들러_스케줄_도착순서_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, 0);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime.plusHours(4));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime.plusHours(3).plusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), arrivalTime.plusHours(3));
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleService.analyzeScheduleArrivalResult(schedule1.getId());

        //then
        Schedule findSchedule = scheduleQueryRepository.findById(schedule1.getId()).orElse(null);
        assertThat(findSchedule).isNotNull();

        String scheduleArrivalResultStr = findSchedule.getScheduleResult().getScheduleArrivalResult();
        List<ScheduleArrivalMember> data = objectMapper.readValue(scheduleArrivalResultStr, ScheduleArrivalResult.class).getMembers();
        assertThat(data).extracting("memberId").containsExactly(member3.getId(), member2.getId(), member1.getId());
    }

    Schedule createSchedule(Member member, Team team, int pointAmount){
        return Schedule.create(member, new TeamValue(team), UUID.randomUUID().toString(), LocalDateTime.now().plusHours(3),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()), pointAmount);
    }
}
