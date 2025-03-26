package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.member.MemberProfileDto;
import aiku_main.dto.member.MemberRegisterDto;
import aiku_main.dto.member.NicknameExistResDto;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.oauth.KakaoOauthHelper;
import aiku_main.repository.EventRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.s3.S3ImageProvider;
import common.domain.event.RecommendEvent;
import common.domain.member.Member;
import common.domain.member.MemberProfileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class MemberRegisterService {
    private final MemberRepository memberRepository;
    private final KakaoOauthHelper kakaoOauthHelper;
    private final PasswordEncoder passwordEncoder;
    private final S3ImageProvider imageProvider;

    private final EventRepository eventRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;

    @Transactional
    public Long register(MemberRegisterDto memberRegisterDto) {
        MemberProfileDto memberProfile = memberRegisterDto.getMemberProfile();

        String idToken = memberRegisterDto.getIdToken();
        String oauthId = kakaoOauthHelper.getOauthInfoByIdToken(idToken).getOid();

        String password = passwordEncoder.encode(oauthId);

        String imgUrl = ""; // S3 이미지 URL
        if (memberProfile.getProfileType().equals(MemberProfileType.IMG)) {
            // S3 저장 로직
            MultipartFile profileImg = memberProfile.getProfileImg();
            imgUrl = imageProvider.upload(profileImg);
        }

        Member member = Member.builder()
                .email(memberRegisterDto.getEmail())
                .nickname(memberRegisterDto.getNickname())
                .oauthId(Long.valueOf(oauthId))
                .password(password)
                .build();

        member.updateProfile(memberProfile.getProfileType(), imgUrl, memberProfile.getProfileCharacter(), memberProfile.getProfileBackground());
        member.updateAuth(memberRegisterDto.getIsServicePolicyAgreed(),
                memberRegisterDto.getIsPersonalInformationPolicyAgreed(),
                memberRegisterDto.getIsLocationPolicyAgreed(),
                memberRegisterDto.getIsMarketingPolicyAgreed());

        memberRepository.save(member);

        if (!memberRegisterDto.getRecommenderNickname().isEmpty()) {
            addRecommender(member, memberRegisterDto.getRecommenderNickname());
        }

        return member.getId();
    }

    private void addRecommender(Member member, String recommenderNickname) {
        Member recommender = memberRepository.findByNickname(recommenderNickname)
                .orElseThrow(MemberNotFoundException::new);

        RecommendEvent recommendEvent = new RecommendEvent(member, recommender);

        eventRepository.save(recommendEvent);

        pointChangeEventPublisher.publish(member.getId(), PointChangeType.PLUS, 10, PointChangeReason.EVENT, recommendEvent.getId());
        pointChangeEventPublisher.publish(recommender.getId(), PointChangeType.PLUS, 10, PointChangeReason.EVENT, recommendEvent.getId());
    }


    public NicknameExistResDto checkNickname(String nickname) {
        boolean exist = memberRepository.existsByNickname(nickname);
        return new NicknameExistResDto(exist);
    }
}
