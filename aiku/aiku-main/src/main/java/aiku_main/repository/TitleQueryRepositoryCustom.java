package aiku_main.repository;

import aiku_main.dto.member.TitleMemberResDto;
import common.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface TitleQueryRepositoryCustom {

    Optional<Long> findTitleMemberIdByMemberIdAndTitleId(Long memberId, Long titleId);
    TitleMemberResDto getTitleMemberResDtoByTitleId(Long titleId);
    List<TitleMemberResDto> getTitleMembers(Long memberId);
    boolean existTitleMember(Long memberId, Long titleId);

    List<Long> findMemberIdsInSchedule(Long scheduleId);
    List<Member> find10kPointsMembersByMemberIds(List<Long> members);
    List<Member> findEarlyArrival10TimesMembersByMemberIds(List<Long> members);
    List<Member> findLateArrival5TimesMembersByMemberIds(List<Long> members);

    List<Member> findLateArrival10TimesMembersByMemberIds(List<Long> members);

    List<Member> findBettingWinning5TimesMembersByMemberIds(List<Long> members);

    List<Member> findBettingLosing10TimesMembersByMemberIds(List<Long> members);
}
