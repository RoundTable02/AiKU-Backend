package aiku_main.repository;

import aiku_main.dto.TitleMemberResDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

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
}
