package aiku_main.repository.racing;

import aiku_main.dto.MemberProfileResDto;
import aiku_main.dto.schedule.result.racing.RacingResult;
import aiku_main.dto.schedule.result.racing.RacingResultMember;
import aiku_main.repository.dto.TeamRacingResultMemberDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.member.QMember;
import common.domain.schedule.QScheduleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.domain.ExecStatus.TERM;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.racing.QRacing.racing;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.team.QTeam.team;
import static common.domain.team.QTeamMember.teamMember;

@RequiredArgsConstructor
@Repository
public class RacingRepositoryCustomImpl implements RacingRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Map<Long, List<TeamRacingResultMemberDto>> findMemberWithTermRacingsInTeam(Long teamId) {
        // 레이싱 신청자 조회
        List<Tuple> firstRacerRacings = query
                .select(
                        member.id,
                        Projections.constructor(
                                TeamRacingResultMemberDto.class,
                                member.id,
                                member.nickname,
                                constructMemberProfileResDto(member),
                                racing.winner.id.eq(scheduleMember.id),
                                teamMember.status)
                )
                .from(racing)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .innerJoin(teamMember).on(teamMember.member.id.eq(scheduleMember.member.id))
                .innerJoin(member).on(member.id.eq(teamMember.member.id))
                .innerJoin(team).on(team.id.eq(teamMember.team.id))
                .where(
                        team.id.eq(teamId),
                        racing.status.eq(ALIVE),
                        racing.raceStatus.eq(TERM)
                )
                .fetch();

        // 레이싱 수락자 조회
        List<Tuple> secondRacerRacings = query
                .select(member.id,
                        Projections.constructor(
                                TeamRacingResultMemberDto.class,
                                member.id,
                                member.nickname,
                                constructMemberProfileResDto(member),
                                racing.winner.id.eq(scheduleMember.id),
                                teamMember.status)
                )
                .from(racing)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .innerJoin(teamMember).on(teamMember.member.id.eq(scheduleMember.member.id))
                .innerJoin(member).on(member.id.eq(teamMember.member.id))
                .innerJoin(team).on(team.id.eq(teamMember.team.id))
                .where(
                        team.id.eq(teamId),
                        racing.status.eq(ALIVE),
                        racing.raceStatus.eq(TERM)
                )
                .fetch();

        Map<Long, List<TeamRacingResultMemberDto>> firstRacerRacingsMap = firstRacerRacings.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(member.id),
                        Collectors.mapping(
                                tuple -> tuple.get(1, TeamRacingResultMemberDto.class),
                                Collectors.toList()
                        )
                ));

        Map<Long, List<TeamRacingResultMemberDto>> secondRacerRacingsMap = secondRacerRacings.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(member.id),
                        Collectors.mapping(
                                tuple -> tuple.get(1, TeamRacingResultMemberDto.class),
                                Collectors.toList()
                        )
                ));

        // 두 맵 합치기
        secondRacerRacingsMap.forEach((id, list) ->
                firstRacerRacingsMap.merge(id, list, (list1, list2) -> Stream.of(list1, list2)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
                )
        );

        return firstRacerRacingsMap;
    }

    @Override
    public List<RacingResult> getRacingResultInSchedule(Long scheduleId) {
        QScheduleMember fRacerScheMem = new QScheduleMember("fRacerScheMem");
        QScheduleMember sRacerScheMem = new QScheduleMember("sRacerScheMem");
        QMember fRacerMem = new QMember("fRacerMem");
        QMember sRacerMem = new QMember("sRacerMem");

        return query
                .select(
                        Projections.constructor(
                        RacingResult.class,
                        constructRacingResultMember(fRacerMem),
                        constructRacingResultMember(sRacerMem),
                        racing.pointAmount,
                        racing.winner.id
                ))
                .from(racing)
                .innerJoin(fRacerScheMem).on(fRacerScheMem.id.eq(racing.firstRacer.id))
                .innerJoin(sRacerScheMem).on(sRacerScheMem.id.eq(racing.secondRacer.id))
                .innerJoin(fRacerMem).on(fRacerMem.id.eq(fRacerScheMem.member.id))
                .innerJoin(sRacerMem).on(sRacerMem.id.eq(sRacerScheMem.member.id))
                .where(
                        fRacerScheMem.schedule.id.eq(scheduleId),
                        racing.raceStatus.eq(TERM),
                        racing.status.eq(ALIVE)
                )
                .fetch();
    }

    private ConstructorExpression<RacingResultMember> constructRacingResultMember(QMember member){
        return Projections.constructor(
                RacingResultMember.class,
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
                member.profile.profileBackground
        );
    }
}
