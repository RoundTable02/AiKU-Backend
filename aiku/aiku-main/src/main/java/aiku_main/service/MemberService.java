package aiku_main.service;

import aiku_main.dto.*;
import aiku_main.repository.MemberReadRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.s3.S3ImageProvider;
import common.domain.member.Member;
import common.domain.member.MemberProfile;
import common.domain.member.MemberProfileType;
import common.domain.title.TitleMember;
import common.domain.value_reference.TitleMemberValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberReadRepository memberReadRepository;
    private final S3ImageProvider s3ImageProvider;

    public MemberResDto getMemberDetail(Member member) {
        MemberProfile profile = member.getProfile();
        MemberProfileResDto memberProfileResDto
                = new MemberProfileResDto(
                        profile.getProfileType(), profile.getProfileImg(),
                profile.getProfileCharacter(), profile.getProfileBackground());

        TitleMemberValue titleMemberValue = member.getMainTitle();
        TitleMemberResDto mainTitle = memberReadRepository.getTitle(titleMemberValue.getId());

        return new MemberResDto(
                member.getId(), member.getNickname(), member.getKakaoId(),
                memberProfileResDto, mainTitle, member.getPoint());
    }

    @Transactional
    public Long updateMember(Member member, MemberUpdateDto memberUpdateDto) {
        MemberProfileDto afterProfile = memberUpdateDto.getMemberProfileDto();
        MemberProfile beforeProfile = member.getProfile();
        MemberProfileType beforeProfileType = beforeProfile.getProfileType();

        String profileImg = "";
        if (afterProfile.getProfileImg().isEmpty()) {
            if (beforeProfileType.equals(MemberProfileType.IMG)
                    && afterProfile.getProfileType().equals(MemberProfileType.CHAR)) {
                // 이미지 -> 캐릭터, 이전 이미지는 삭제
                s3ImageProvider.deleteImageFromS3(beforeProfile.getProfileImg());
            }
        } else {
            // 이미지 변경된 경우
            if (beforeProfileType.equals(MemberProfileType.IMG)) {
                // 이미지 -> 이미지, 이전 이미지는 삭제
                s3ImageProvider.deleteImageFromS3(beforeProfile.getProfileImg());
            }
            profileImg = s3ImageProvider.upload(afterProfile.getProfileImg());
        }

        member.updateMember(
                memberUpdateDto.getNickname(), afterProfile.getProfileType(),
                profileImg, afterProfile.getProfileCharacter(), afterProfile.getProfileBackground()
        );

        return member.getId();
    }

    @Transactional
    public Long deleteMember(Member member) {
        memberRepository.delete(member);

        return member.getId();
    }

    @Transactional
    public Long updateTitle(Member member, Long titleMemberId) {
        TitleMember titleMember = memberReadRepository.getTitleMemberByTitleMemberId(titleMemberId);
        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);

        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        return member.getId();
    }
}
