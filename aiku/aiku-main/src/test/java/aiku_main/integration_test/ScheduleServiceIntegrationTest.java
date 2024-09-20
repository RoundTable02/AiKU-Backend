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
    @DisplayName("스케줄 등록-권한O/X")
    void addSchedule() {
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
    @DisplayName("스케줄 수정-권한O/X")
    void updateSchedule() {
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

        //권한 x
        assertThatThrownBy(() -> scheduleService.updateSchedule(member2, schedule.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("스케줄 수정-1시간 이내의 스케줄 수정 금지")
    void updateScheduleWithTimeLimit() {
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
    @DisplayName("스케줄 입장-기본/중복 입장,권한O/X")
    void enterSchedule() {
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

        //권한x-그룹에 속해 있지 않을 때
        assertThatThrownBy(() -> scheduleService.enterSchedule(member3, team.getId(), schedule.getId(), enterDto)).isInstanceOf(NoAuthorityException.class);
        //중복 요청
        assertThatThrownBy(() -> scheduleService.enterSchedule(member1, team.getId(), schedule.getId(), enterDto)).isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("스케줄 입장-실행, 종료된 스케줄일때")
    void enterScheduleNotWait() {
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
    @DisplayName("스케줄 퇴장-기본/중복 퇴장,권한O/X")
    void exitSchedule() {
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

        //권한x-그룹에 속해 있지 않을 때
        assertThatThrownBy(() -> scheduleService.exitSchedule(member4, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
        //중복 요청
        assertThatThrownBy(() -> scheduleService.exitSchedule(member2, team.getId(), schedule.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("스케줄 퇴장-스케줄 멤버가 없어 자동 삭제")
    void exitScheduleWithNoMember() {
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
    @DisplayName("스케줄 퇴장-방장 변경")
    void exitScheduleWithChangeOwner() {
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

    @DisplayName("스케줄 퇴장-실행, 종료된 스케줄일때")
    void exitScheduleNotWait() {
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
    @DisplayName("스케줄 상세 조회-권한O/X")
    void getScheduleDetail() {
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

        //권한 x-스케줄 멤버가 아닐때
        assertThatThrownBy(() -> scheduleService.getScheduleDetail(member4, team.getId(), schedule.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("그룹의 스케줄 목록 조회-필터링x,권한O/X")
    void getTeamScheduleList() {
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

        //권한 x
        assertThatThrownBy(() -> scheduleService.getTeamScheduleList(member4, team.getId(), new SearchDateCond(), 1)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("그룹의 스케줄 목록 조회-필터링o")
    void getTeamScheduleListWithFilter() {
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
    @DisplayName("멤버의 스케줄 목록 조회-필터링x")
    void getMemberScheduleList() {
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
    @DisplayName("멤버의 스케줄 목록 조회-필터링o")
    void getMemberScheduleListWithFilter() {
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
