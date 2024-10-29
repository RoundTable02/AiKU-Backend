package aiku_main.repository;

import aiku_main.dto.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.title.TitleMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static common.domain.title.QTitleMember.titleMember;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public TitleMemberResDto getTitle(Long titleId) {
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
    public TitleMember getTitleMemberByTitleMemberId(Long titleMemberId) {
        return query.selectFrom(titleMember)
                .where(titleMember.id.eq(titleMemberId))
                .fetchOne();
    }

    @Override
    public boolean existTitleMember(Long memberId, Long titleMemberId) {
        Long count = query.select(titleMember.count())
                .from(titleMember)
                .where(titleMember.id.eq(titleMemberId),
                        titleMember.member.id.eq(memberId))
                .fetchOne();
        return count != null && count > 0;
    }
}
