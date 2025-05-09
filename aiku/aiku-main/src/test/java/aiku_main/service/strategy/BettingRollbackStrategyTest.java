package aiku_main.service.strategy;

import aiku_main.repository.betting.BettingRepository;
import common.domain.Status;
import common.domain.betting.Betting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class BettingRollbackStrategyTest {

    @Autowired
    BettingRollbackStrategy bettingRollbackStrategy;
    @Autowired
    BettingRepository bettingRepository;

    @Test
    void execute() {
        //given
        Betting betting = Betting.create(null, null, 10);
        bettingRepository.save(betting);

        //when
        bettingRollbackStrategy.execute(
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