package aiku_main.repository.racing;

import aiku_main.dto.MemberProfileResDto;
import aiku_main.dto.schedule.result.racing.RacingResult;
import aiku_main.dto.schedule.result.racing.RacingResultMember;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.member.QMember;
import common.domain.schedule.QScheduleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static common.domain.ExecStatus.TERM;
import static common.domain.Status.ALIVE;
import static common.domain.racing.QRacing.racing;

@RequiredArgsConstructor
@Repository
public class RacingRepositoryCustomImpl implements RacingRepositoryCustom {

    private final JPAQueryFactory query;

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
