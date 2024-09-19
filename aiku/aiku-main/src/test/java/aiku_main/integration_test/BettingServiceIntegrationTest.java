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
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static common.domain.ExecStatus.WAIT;
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

    Schedule schedule1;

    @BeforeEach
    void beforeEach(){
        member1 = Member.create("member1");
        member2 = Member.create("member2");
        member3 = Member.create("member3");
        noScheduleMember = Member.create("noScheduleMember");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(noScheduleMember);

        schedule1 = Schedule.create(member1, null, "schedule1", LocalDateTime.now(),
                new Location("loc1", 1.0, 1.0), 0);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
        scheduleRepository.deleteAll();
    }

    @Test
    @DisplayName("베팅 등록-권한O/X,기본/중복요청")
    void addBetting() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 0);
        Long bettingId = bettingService.addBetting(member1, schedule1.getId(), bettingDto);

        em.flush();
        em.clear();

        //then
        Betting betting = bettingRepository.findById(bettingId).orElse(null);
        ScheduleMember bettor = scheduleRepository.findScheduleMember(member1.getId(), schedule1.getId()).orElse(null);
        ScheduleMember betee = scheduleRepository.findScheduleMember(member2.getId(), schedule1.getId()).orElse(null);

        assertThat(betting).isNotNull();
        assertThat(betting.getBettor().getId()).isEqualTo(bettor.getId());
        assertThat(betting.getBetee().getId()).isEqualTo(betee.getId());

        //스케줄에 속하지 않을 때
        assertThatThrownBy(() -> bettingService.addBetting(noScheduleMember, schedule1.getId(), bettingDto)).isInstanceOf(NoAuthorityException.class);
        //중복 요청
        assertThatThrownBy(() -> bettingService.addBetting(member1, schedule1.getId(), bettingDto)).isInstanceOf(CanNotBettingException.class);
    }

    @Test
    @DisplayName("베팅 등록-포인트 부족")
    void addBettingWithNotEnoughPoint() {
        //when
        BettingAddDto bettingDto = new BettingAddDto(member2.getId(), 1000);

        //then
        assertThatThrownBy(() -> bettingService.addBetting(member1, schedule1.getId(), bettingDto)).isInstanceOf(NotEnoughPoint.class);
    }

    @Test
    @DisplayName("베팅 취소-권한O/X,기본/중복요청")
    void cancelBetting() {
        //given
        ScheduleMember bettor = scheduleRepository.findScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

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

        //중복 요청
        assertThatThrownBy(() -> bettingService.cancelBetting(member1, schedule1.getId(), betting.getId())).isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("베팅 취소-권한X")
    void cancelBettingNoAuthority() {
        //given
        ScheduleMember bettor = scheduleRepository.findScheduleMember(member1.getId(), schedule1.getId()).orElseThrow();
        ScheduleMember betee = scheduleRepository.findScheduleMember(member2.getId(), schedule1.getId()).orElseThrow();

        Betting betting = Betting.create(new ScheduleMemberValue(bettor), new ScheduleMemberValue(betee), 100);
        em.persist(betting);

        em.flush();
        em.clear();

        //when
        //스케줄 멤버 아님
        assertThatThrownBy(() -> bettingService.cancelBetting(noScheduleMember, schedule1.getId(), betting.getId())).isInstanceOf(NoSuchElementException.class);
        //베터가 아님
        assertThatThrownBy(() -> bettingService.cancelBetting(member2, schedule1.getId(), betting.getId())).isInstanceOf(NoAuthorityException.class);
    }
}