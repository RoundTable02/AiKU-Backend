package aiku_main.repository;

import aiku_main.dto.TitleMemberResDto;
import common.domain.title.TitleMember;

import java.util.List;

public interface MemberQueryRepository {
    TitleMemberResDto getTitle(Long titleId);
    List<TitleMemberResDto> getTitleMembers(Long memberId);
    TitleMember getTitleMemberByTitleMemberId(Long titleMemberId);
    boolean existTitleMember(Long memberId, Long titleId);
}
