package aiku_main.service.strategy;

import aiku_main.repository.BettingQueryRepository;
import common.domain.Betting;
import common.domain.Status;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BettingCancelRollbackStrategyTest {

    @Autowired
    BettingQueryRepository bettingRepository;
    @Autowired
    BettingCancelRollbackStrategy bettingCancelRollbackStrategy;

    @Test
    void execute() {
        //given
        Betting betting = Betting.create(null, null, 10);
        bettingRepository.save(betting);

        //when
        bettingCancelRollbackStrategy.execute(
                null,
                null,
                10,
                null,
                betting.getId()
        );

        //then
        assertThat(betting.getStatus()).isEqualTo(Status.ERROR);
    }
}