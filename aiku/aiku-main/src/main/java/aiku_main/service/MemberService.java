package aiku_main.service;

import aiku_main.dto.*;
import aiku_main.exception.TitleException;
import aiku_main.repository.MemberReadRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.s3.S3ImageProvider;
import common.domain.ServiceAgreement;
import common.domain.member.Member;
import common.domain.member.MemberProfile;
import common.domain.member.MemberProfileType;
import common.domain.title.TitleMember;
import common.domain.value_reference.TitleMemberValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static common.response.status.BaseErrorCode.MEMBER_NOT_WITH_TITLE;

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

        TitleMemberResDto mainTitle = null;
        // 장착된 칭호가 존재하는 경우에만 조회
        if (!Objects.isNull(titleMemberValue)) {
            mainTitle = memberReadRepository.getTitle(titleMemberValue.getId());
        }

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
        validateTitleMember(member.getId(), titleMemberId);

        TitleMember titleMember = memberReadRepository.getTitleMemberByTitleMemberId(titleMemberId);
        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);

        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        return member.getId();
    }

    private void validateTitleMember(Long memberId, Long titleMemberId) {
        if (!memberReadRepository.existTitleMember(memberId, titleMemberId)) {
            throw new TitleException(MEMBER_NOT_WITH_TITLE);
        }
    }

    @Transactional
    public Long logout(Member member) {
        // 멤버 리프레시 토큰 삭제
        member.logout();

        return member.getId();
    }


    @Transactional
    public Long updateAuth(Member member, AuthorityUpdateDto authorityUpdateDto) {
        member.updateAuth(authorityUpdateDto.isServicePolicyAgreed(), authorityUpdateDto.isPersonalInformationPolicyAgreed(),
                authorityUpdateDto.isLocationPolicyAgreed(), authorityUpdateDto.isMarketingPolicyAgreed());

        return member.getId();
    }

    public AuthorityResDto getAuthDetail(Member member) {
        ServiceAgreement serviceAgreement = member.getServiceAgreement();

        return new AuthorityResDto(
                serviceAgreement.isServicePolicyAgreed(), serviceAgreement.isPersonalInformationPolicyAgreed(),
                serviceAgreement.isLocationPolicyAgreed(), serviceAgreement.isMarketingPolicyAgreed());
    }

    public DataResDto<List<TitleMemberResDto>> getMemberTitles(Member member) {
        List<TitleMemberResDto> titleMembers = memberReadRepository.getTitleMembers(member.getId());

        return new DataResDto(1, titleMembers);
    }
}
