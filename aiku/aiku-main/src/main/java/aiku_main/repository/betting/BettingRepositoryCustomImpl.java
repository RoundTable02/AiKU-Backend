package aiku_main.repository.betting;

import aiku_main.dto.MemberProfileResDto;
import aiku_main.dto.schedule.result.betting.BettingResult;
import aiku_main.dto.schedule.result.betting.BettingResultMember;
import aiku_main.repository.dto.TeamBettingResultMemberDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.betting.Betting;
import common.domain.ExecStatus;
import common.domain.member.QMember;
import common.domain.schedule.QScheduleMember;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static common.domain.ExecStatus.TERM;
import static common.domain.Status.ALIVE;
import static common.domain.betting.QBetting.betting;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.team.QTeam.team;
import static common.domain.team.QTeamMember.teamMember;

@RequiredArgsConstructor
public class BettingRepositoryCustomImpl implements BettingRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public boolean existBettorInSchedule(Long scheduleMemberIdOfBettor, Long scheduleId) {
        Long count = query
                .select(betting.count())
                .from(betting)
                .join(scheduleMember).on(scheduleMember.id.eq(scheduleMemberIdOfBettor))
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        betting.bettor.id.eq(scheduleMemberIdOfBettor),
                        betting.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus) {
        return query.selectFrom(betting)
                .join(scheduleMember).on(scheduleMember.id.eq(betting.bettor.id))
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        betting.bettingStatus.eq(bettingStatus),
                        betting.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public Map<Long, List<TeamBettingResultMemberDto>> findMemberTermBettingsInTeam(Long teamId) {
        List<Tuple> bettings = query
                .select(member.id,
                        Projections.constructor(
                                TeamBettingResultMemberDto.class,
                                member.id,
                                member.nickname,
                                constructMemberProfileResDto(member),
                                betting.isWinner,
                                teamMember.status
                        )
                )
                .from(betting)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(betting.bettor.id))
                .innerJoin(teamMember).on(teamMember.member.id.eq(scheduleMember.member.id))
                .innerJoin(member).on(member.id.eq(teamMember.member.id))
                .innerJoin(team).on(team.id.eq(teamMember.team.id))
                .where(
                        team.id.eq(teamId),
                        betting.status.eq(ALIVE),
                        betting.bettingStatus.eq(TERM)
                )
                .fetch();

        return bettings.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(member.id),
                        Collectors.mapping(              // Value: TeamBettingResultMemberDto 리스트
                                tuple -> tuple.get(1, TeamBettingResultMemberDto.class),
                                Collectors.toList()
                        )
                ));
    }

    @Override
    public List<BettingResult> getBettingResultsInSchedule(Long scheduleId) {
        QScheduleMember betterScheMem = new QScheduleMember("betterScheMem");
        QScheduleMember beteeScheMem = new QScheduleMember("beteeScheMem");
        QMember bettorMem = new QMember("bettorMem");
        QMember beteeMem = new QMember("beteeMem");

        return query
                .select(Projections.constructor(
                        BettingResult.class,
                        constructScheduleBettingMember(bettorMem),
                        constructScheduleBettingMember(beteeMem),
                        betting.pointAmount
                ))
                .from(betting)
                .innerJoin(betterScheMem).on(betterScheMem.id.eq(betting.bettor.id))
                .innerJoin(beteeScheMem).on(beteeScheMem.id.eq(betting.betee.id))
                .innerJoin(bettorMem).on(bettorMem.id.eq(betterScheMem.member.id))
                .innerJoin(beteeMem).on(beteeMem.id.eq(beteeScheMem.member.id))
                .where(
                        betterScheMem.schedule.id.eq(scheduleId),
                        betting.bettingStatus.eq(TERM),
                        betting.status.eq(ALIVE)
                )
                .fetch();
    }

    private ConstructorExpression<BettingResultMember> constructScheduleBettingMember(QMember member){
        return Projections.constructor(
                BettingResultMember.class,
                member.id,
                member.nickname,
                constructMemberProfileResDto(member)
        );
    }

    private ConstructorExpression<MemberProfileResDto> constructMemberProfileResDto(QMember member){
        return Projections.constructor(
                MemberProfileResDto.class,
                member.profile.profileType,
                member.profile.profileImg,
                member.profile.profileCharacter,
                member.profile.profileBackground);
    }
}
