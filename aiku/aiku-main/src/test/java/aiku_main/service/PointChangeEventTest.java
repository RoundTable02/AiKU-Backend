package aiku_main.service;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.application_event.handler.PointChangeEventHandler;
import aiku_main.repository.PointLogRepository;
import common.domain.log.EventLog;
import common.domain.log.PointLog;
import common.domain.log.PointLogStatus;
import common.domain.member.Member;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import common.domain.value_reference.MemberValue;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class PointChangeEventTest {

    @Autowired
    PointChangeEventHandler handler;

    @Autowired
    EntityManager em;

    @Autowired
    PointLogRepository pointLogRepository;

    Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
                .kakaoId(123L)
                .nickname("nickname1")
                .password("password1")
                .email("asdasd@gmail.com")
                .build();

        member.updateProfile(MemberProfileType.CHAR, "", MemberProfileCharacter.C01, MemberProfileBackground.PURPLE);

        member.updateAuth(true, true, false, true);

        em.persist(member);
    }

    @Test
    void 포인트_변화_처리() {
        handler.pointChangeEvent(
                new PointChangeEvent(member.getId(), PointChangeType.PLUS, 100, PointChangeReason.EVENT, 11L)
        );

        List<PointLog> pointLogs = pointLogRepository.findAll();

        assertThat(member.getPoint()).isEqualTo(100);
        assertThat(pointLogs.size()).isEqualTo(1);
        assertThat(pointLogs.get(0).getMemberId()).isEqualTo(member.getId());
        assertThat(pointLogs.get(0).getPointLogStatus()).isEqualTo(PointLogStatus.ACCEPT);
        assertThat(pointLogs.get(0)).isExactlyInstanceOf(EventLog.class);

        EventLog eventLog = (EventLog) pointLogs.get(0);
        assertThat(eventLog.getEvent().getId()).isEqualTo(11L);
    }
}