package login.service;

import common.domain.event.RecommendEvent;
import common.domain.member.Member;
import common.domain.member.MemberProfileType;
import login.application_event.event.PointChangeReason;
import login.application_event.event.PointChangeType;
import login.application_event.publisher.PointChangeEventPublisher;
import login.dto.MemberProfileDto;
import login.dto.MemberRegisterDto;
import login.exception.MemberNotFoundException;
import login.repository.EventRepository;
import login.repository.MemberRepository;
import login.s3.S3ImageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class MemberRegisterService {
    private final MemberRepository memberRepository;
    private final S3ImageProvider imageProvider;

    private final EventRepository eventRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;

    @Value("${custom.password}")
    private String password;

    @Transactional
    public Long register(MemberRegisterDto memberRegisterDto) {
        MemberProfileDto memberProfile = memberRegisterDto.getMemberProfile();

        String imgUrl = ""; // S3 이미지 URL
        if (memberProfile.getProfileType().equals(MemberProfileType.IMG)) {
            // S3 저장 로직
            MultipartFile profileImg = memberProfile.getProfileImg();
            imgUrl = imageProvider.upload(profileImg);
        }

        Member member = Member.register(memberRegisterDto.getEmail(), memberRegisterDto.getNickname(), password,
                memberProfile.getProfileType(), imgUrl, memberProfile.getProfileCharacter(),
                memberProfile.getProfileBackground(),
                memberRegisterDto.getIsServicePolicyAgreed(), memberRegisterDto.getIsPersonalInformationPolicyAgreed(),
                memberRegisterDto.getIsLocationPolicyAgreed(), memberRegisterDto.getIsMarketingPolicyAgreed()
        );

        memberRepository.save(member);

        addRecommender(member, memberRegisterDto.getRecommenderNickname());

        return member.getId();
    }

    private void addRecommender(Member member, String recommenderNickname) {
        Member recommender = memberRepository.findByNickname(recommenderNickname)
                .orElseThrow(() -> new MemberNotFoundException());

        RecommendEvent recommendEvent = new RecommendEvent(member, recommender);

        eventRepository.save(recommendEvent);

        pointChangeEventPublisher.publish(member, PointChangeType.PLUS, 10, PointChangeReason.EVENT, recommendEvent.getId());
        pointChangeEventPublisher.publish(recommender, PointChangeType.PLUS, 10, PointChangeReason.EVENT, recommendEvent.getId());
    }


}
