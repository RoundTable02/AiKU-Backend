package aiku_main.repository;

import aiku_main.dto.MemberProfileResDto;
import aiku_main.repository.dto.TeamRacingResultMemberDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.Racing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.domain.ExecStatus.TERM;
import static common.domain.QRacing.racing;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
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
                .select(member.id, Projections.constructor(TeamRacingResultMemberDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        racing.winner.id.eq(scheduleMember.id), teamMember.status))
                .from(racing)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .innerJoin(teamMember).on(teamMember.member.id.eq(scheduleMember.member.id))
                .innerJoin(member).on(member.id.eq(teamMember.member.id))
                .innerJoin(team).on(team.id.eq(teamMember.team.id))
                .where(team.id.eq(teamId),
                        racing.status.eq(ALIVE),
                        racing.raceStatus.eq(TERM))
                .fetch();

        // 레이싱 수락자 조회
        List<Tuple> secondRacerRacings = query
                .select(member.id, Projections.constructor(TeamRacingResultMemberDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        racing.winner.id.eq(scheduleMember.id), teamMember.status))
                .from(racing)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .innerJoin(teamMember).on(teamMember.member.id.eq(scheduleMember.member.id))
                .innerJoin(member).on(member.id.eq(teamMember.member.id))
                .innerJoin(team).on(team.id.eq(teamMember.team.id))
                .where(team.id.eq(teamId),
                        racing.status.eq(ALIVE),
                        racing.raceStatus.eq(TERM))
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
    public List<Racing> findTermRacingsInSchedule(Long scheduleId) {
        return query.selectFrom(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .join(scheduleMember.schedule, schedule)
                .where(schedule.id.eq(scheduleId),
                        racing.raceStatus.eq(TERM))
                .fetch();
    }
}
