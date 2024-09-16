package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QBetting;
import lombok.RequiredArgsConstructor;

import static common.domain.QBetting.betting;

@RequiredArgsConstructor
public class BettingRepositoryCustomImpl implements BettingRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public boolean existBettorMember(Long scheduleMemberId, Long bettingId) {
        query.select()
                .from(betting)
                .where(betting.bettor.id.eq())
    }
}
