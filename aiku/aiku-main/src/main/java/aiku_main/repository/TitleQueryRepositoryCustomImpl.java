package aiku_main.repository;

import aiku_main.dto.TitleMemberResDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static common.domain.QBetting.betting;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.title.QTitle.title;
import static common.domain.title.QTitleMember.titleMember;

@RequiredArgsConstructor
public class TitleQueryRepositoryCustomImpl implements TitleQueryRepositoryCustom {

    private final JPAQueryFactory query;


    @Override
    public Optional<Long> findTitleMemberIdByMemberIdAndTitleId(Long memberId, Long titleId) {
        Long titleMemberId = query.select(titleMember.id)
                .from(titleMember)
                .where(titleMember.member.id.eq(memberId), titleMember.title.id.eq(titleId))
                .fetchOne();

        return Optional.ofNullable(titleMemberId);
    }

    @Override
    public TitleMemberResDto getTitleMemberResDtoByTitleId(Long titleId) {
        return query.select(Projections.constructor(TitleMemberResDto.class,
                        titleMember.id, titleMember.title.titleName, titleMember.title.titleDescription, titleMember.title.titleImg))
                .from(titleMember)
                .where(titleMember.id.eq(titleId))
                .fetchOne();
    }

    @Override
    public List<TitleMemberResDto> getTitleMembers(Long memberId) {
        return query.select(Projections.constructor(TitleMemberResDto.class,
                        titleMember.id, titleMember.title.titleName, titleMember.title.titleDescription, titleMember.title.titleImg))
                .from(titleMember)
                .where(titleMember.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public boolean existTitleMember(Long memberId, Long titleId) {
        Long count = query.select(titleMember.count())
                .from(titleMember)
                .where(titleMember.title.id.eq(titleId),
                        titleMember.member.id.eq(memberId))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<Long> findMemberIdsInSchedule(Long scheduleId) {
        return query.select(member.id)
                .from(schedule)
                .innerJoin(schedule.scheduleMembers, scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId))
                .fetch();
    }


    @Override
    public List<Member> find10kPointsMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .where(member.id.in(members), member.point.goe(10000))
                .fetch();
    }

    @Override
    public List<Member> findEarlyArrival10TimesMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(member.id.in(members), scheduleMember.arrivalTimeDiff.loe(0), List<Long> members)
                .groupBy(member.id)
                .having(member.id.count().eq(10L))
                .fetch();
    }

    @Override
    public List<Member> findLateArrival5TimesMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(member.id.in(members), scheduleMember.arrivalTimeDiff.goe(0))
                .groupBy(member.id)
                .having(member.id.count().eq(5L))
                .fetch();
    }

    @Override
    public List<Member> findLateArrival10TimesMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(member.id.in(members), scheduleMember.arrivalTimeDiff.goe(0))
                .groupBy(member.id)
                .having(member.id.count().eq(10L))
                .fetch();
    }

    @Override
    public List<Member> findBettingWinning5TimesMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .from(betting)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(betting.bettor.id))
                .where(member.id.in(members), betting.isWinner)
                .groupBy(member.id)
                .having(member.id.count().eq(5L))
                .fetch();
    }

    @Override
    public List<Member> findBettingWinning10TimesMembersByMemberIds(List<Long> members) {
        return query.select(member)
                .from(betting)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(betting.bettor.id))
                .where(member.id.in(members), betting.isWinner)
                .groupBy(member.id)
                .having(member.id.count().eq(10L))
                .fetch();
    }
}
