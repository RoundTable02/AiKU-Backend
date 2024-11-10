package aiku_main.repository;

import aiku_main.dto.TitleMemberResDto;

import java.util.List;
import java.util.Optional;

public interface TitleQueryRepositoryCustom {

    Optional<Long> findTitleMemberIdByMemberIdAndTitleId(Long memberId, Long titleId);
    TitleMemberResDto getTitleMemberResDtoByTitleId(Long titleId);
    List<TitleMemberResDto> getTitleMembers(Long memberId);
    boolean existTitleMember(Long memberId, Long titleId);
}
