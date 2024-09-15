package aiku_main.repository;

import aiku_main.dto.MemberProfileResDto;
import aiku_main.dto.ScheduleMemberResDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QScheduleMember;
import common.domain.Status;
import common.domain.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static common.domain.QScheduleMember.scheduleMember;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class ScheduleReadRepositoryImpl implements ScheduleReadRepository{

    private final JPAQueryFactory query;

    @Override
    public List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId) {
        return query
                .select(Projections.constructor(ScheduleMemberResDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        member.point))
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member).fetchJoin()
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .orderBy(scheduleMember.createdAt.asc())
                .fetch();
    }
}
