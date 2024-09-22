package aiku_main.integration_test;

import aiku_main.dto.BettingAddDto;
import aiku_main.exception.CanNotBettingException;
import aiku_main.repository.BettingRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleRepository;
import aiku_main.service.BettingService;
import common.domain.*;
import common.domain.member.Member;
import common.domain.value_reference.ScheduleMemberValue;
import common.exception.BaseException;
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import common.exception.PaidMemberLimitException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static common.domain.ExecStatus.*;
import static common.domain.Status.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class BettingServiceIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    BettingService bettingService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    BettingRepository bettingRepository;

    Member member1;
    Member member2;
    Member member3;
    Member noScheduleMember;
    Member freeMember;

    Schedule schedule1;

    @BeforeEach
    void beforeEach(){
        member1 = Member.create("member1");
        member2 = Member.create("member2");
        member3 = Member.create("member3");
        freeMember = Member.create("freeMember");
        noScheduleMember = Member.create("noScheduleMember");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(freeMember);
        em.persist(noScheduleMember);

        schedule1 = Schedule.create(member1, null, "schedule1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 1000);
        schedule1.addScheduleMember(member2, false, 1000);
        schedule1.addScheduleMember(member3, false, 1000);
        schedule1.addScheduleMember(freeMember, false, 0);
        em.persist(schedule1);
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
        scheduleRepository.deleteAll();
    }

    @Test
    void 베팅_등록() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 0);
        Long bettingId = bettingService.addBetting(member1, schedule1.getId(), bettingDto);

        em.flush();
        em.clear();

        //then
        Betting betting = bettingRepository.findById(bettingId).orElse(null);
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElse(null);
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElse(null);

        assertThat(betting).isNotNull();
        assertThat(betting.getBettor().getId()).isEqualTo(bettor.getId());
        assertThat(betting.getBetee().getId()).isEqualTo(betee.getId());
    }

    @Test
    void 베팅_등록_대기스케줄x() {
        //given
        schedule1.setScheduleStatus(TERM);

        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 0);
        assertThatThrownBy(() -> bettingService.addBetting(member1, schedule1.getId(), bettingDto)).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 베팅_등록_스케줄멤버x() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 0);
        assertThatThrownBy(() -> bettingService.addBetting(noScheduleMember, schedule1.getId(), bettingDto)).isInstanceOf(BaseException.class);
    }

    @Test
    void 베팅_등록_깍두기멤버() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(freeMember.getId(), 0);
        assertThatThrownBy(() -> bettingService.addBetting(noScheduleMember, schedule1.getId(), bettingDto)).isInstanceOf(PaidMemberLimitException.class);
    }

    @Test
    void 베팅_등록_중복() {
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElse(null);
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElse(null);

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 0);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 0);
        assertThatThrownBy(() -> bettingService.addBetting(member1, schedule1.getId(), bettingDto)).isInstanceOf(CanNotBettingException.class);
    }

    @Test
    void 베팅_등록_포인트부족() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 1000);

        //then
        assertThatThrownBy(() -> bettingService.addBetting(member1, schedule1.getId(), bettingDto)).isInstanceOf(NotEnoughPoint.class);
    }

    @Test
    void 베팅_취소() {
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        bettingService.cancelBetting(member1, schedule1.getId(), betting.getId());

        em.flush();
        em.clear();

        //then
        Betting findBetting = bettingRepository.findById(betting.getId()).orElseThrow();
        assertThat(findBetting.getBettingStatus()).isEqualTo(WAIT);
        assertThat(findBetting.getStatus()).isEqualTo(DELETE);
    }

    @Test
    void 베팅_취소_중복() {
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        bettingService.cancelBetting(member1, schedule1.getId(), betting.getId());
        assertThatThrownBy(() -> bettingService.cancelBetting(member1, schedule1.getId(), betting.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    void 베팅_취소_베터x() {
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> bettingService.cancelBetting(member2, schedule1.getId(), betting.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 베팅_취소_스케줄멤버x() {
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> bettingService.cancelBetting(noScheduleMember, schedule1.getId(), betting.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 이벤트핸들러_스케줄퇴장_퇴장멤버가_베터인_베팅제거(){
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        bettingService.exitSchedule_deleteBettingForBettor(member1.getId(), bettor.getId(), schedule1.getId());
        em.flush();
        em.clear();

        //then
        Betting findBetting = bettingRepository.findById(betting.getId()).orElse(null);
        assertThat(findBetting).isNotNull();
        assertThat(findBetting.getStatus()).isEqualTo(DELETE);
    }

    @Test
    void 이벤트핸들러_스케줄퇴장_퇴장멤버가_베티인_베팅제거(){
        //given
        ScheduleMember bettor = scheduleRepository.findAliveScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findAliveScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        bettingService.exitSchedule_deleteBettingForBettor(member2.getId(), bettor.getId(), schedule1.getId());
        em.flush();
        em.clear();

        //then
        Betting findBetting = bettingRepository.findById(betting.getId()).orElse(null);
        assertThat(findBetting).isNotNull();
        assertThat(findBetting.getStatus()).isEqualTo(DELETE);
    }
}