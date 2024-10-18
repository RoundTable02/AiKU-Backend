package aiku_main.repository;

import aiku_main.dto.TitleMemberResDto;
import common.domain.title.TitleMember;

import java.util.List;
import java.util.Optional;

public interface MemberReadRepository {
    TitleMemberResDto getTitle(Long titleId);
    List<TitleMemberResDto> getTitleMembers(Long memberId);
    TitleMember getTitleMemberByTitleMemberId(Long titleMemberId);

    boolean existTitleMember(Long memberId, Long titleId);
}
