package common.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BettingTest {

    @Test
    void create() {
        Long memberId = 1L;
        Long beteeId = 2L;
        int pointAmount = 3000;
        Betting betting = Betting.create(memberId, beteeId, pointAmount);

        assertThat(betting.getBettor().getId()).isEqualTo(memberId);
        assertThat(betting.getBetee().getId()).isEqualTo(beteeId);
        assertThat(betting.getPointAmount()).isEqualTo(pointAmount);
        assertThat(betting.getBettingStatus()).isEqualTo(ExecStatus.WAIT);
        assertThat(betting.getStatus()).isEqualTo(Status.ALIVE);
    }
}