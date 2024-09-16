package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QBetting;
import lombok.RequiredArgsConstructor;

import static common.domain.QBetting.betting;

@RequiredArgsConstructor
public class BettingRepositoryCustomImpl implements BettingRepositoryCustom{

    private final JPAQueryFactory query;
}
