package login.service;

import common.domain.Status;
import common.domain.event.RecommendEvent;
import common.domain.member.*;
import jakarta.persistence.EntityManager;
import login.application_event.publisher.PointChangeEventPublisher;
import login.dto.MemberProfileDto;
import login.dto.MemberRegisterDto;
import login.dto.NicknameExistDto;
import login.dto.NicknameExistResDto;
import gateway.exception.MemberNotFoundException;
import login.oauth.KakaoOauthHelper;
import login.oauth.OauthInfo;
import login.repository.EventRepository;
import gateway.repository.MemberReadRepository;
import login.repository.MemberRepository;
import login.s3.S3ImageProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class MemberRegisterServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberReadRepository memberReadRepository;
    @MockBean
    KakaoOauthHelper kakaoOauthHelper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    S3ImageProvider imageProvider;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    PointChangeEventPublisher pointChangeEventPublisher;

    @Autowired
    MemberRegisterService memberRegisterService;

    @Test
    void 멤버_추천인X_정상_회원가입() {
        // given
        OauthInfo mockOauthInfo = new OauthInfo("123");
        when(kakaoOauthHelper.getOauthInfoByIdToken(any())).thenReturn(mockOauthInfo);

        MemberProfileDto memberProfileDto = new MemberProfileDto(MemberProfileType.CHAR, null,
                MemberProfileCharacter.C01, MemberProfileBackground.BLUE);
        MemberRegisterDto memberRegisterDto = new MemberRegisterDto(
                "nickname1", "asd@gmail.com", "idToken", memberProfileDto,
                true, true, true, true, ""
        );

        // when
        Long registerMemberId = memberRegisterService.register(memberRegisterDto);

        // then
        Member member = memberRepository.findById(registerMemberId).orElseThrow(() -> new MemberNotFoundException());

        assertThat(member.getNickname()).isEqualTo("nickname1");
        assertThat(member.getEmail()).isEqualTo("asd@gmail.com");
        assertThat(member.getKakaoId()).isEqualTo(123L);
        assertThat(member.getRole()).isEqualTo(MemberRole.MEMBER);
        assertThat(member.getPoint()).isEqualTo(0);
        assertThat(member.getStatus()).isEqualTo(Status.ALIVE);

        MemberProfile profile = member.getProfile();
        assertThat(profile.getProfileImg()).isBlank();
        assertThat(profile.getProfileType()).isEqualTo(MemberProfileType.CHAR);
        assertThat(profile.getProfileCharacter()).isEqualTo(MemberProfileCharacter.C01);
        assertThat(profile.getProfileBackground()).isEqualTo(MemberProfileBackground.BLUE);
    }

    @Test
    void 멤버_추천인O_정상_회원가입() {
        // given
        OauthInfo mockOauthInfo = new OauthInfo("123");
        when(kakaoOauthHelper.getOauthInfoByIdToken(any())).thenReturn(mockOauthInfo);

        MemberProfileDto recommenderProfileDto = new MemberProfileDto(MemberProfileType.CHAR, null,
                MemberProfileCharacter.C01, MemberProfileBackground.BLUE);
        MemberRegisterDto recommenderRegisterDto = new MemberRegisterDto(
                "recommender", "asd@gmail.com", "rIdToken", recommenderProfileDto,
                true, true, true, true, ""
        );
        Long recommenderId = memberRegisterService.register(recommenderRegisterDto);

        MemberProfileDto memberProfileDto = new MemberProfileDto(MemberProfileType.CHAR, null,
                null, null);
        MemberRegisterDto memberRegisterDto = new MemberRegisterDto(
                "nickname1", "asd@gmail.com", "mIdToken", memberProfileDto,
                true, true, true, true,
                "recommender"
        );

        // when
        Long registerMemberId = memberRegisterService.register(memberRegisterDto);

        // then
        RecommendEvent recommendEvent = eventRepository.findRecommendEventByMemberId(registerMemberId)
                .orElseThrow(() -> new NoSuchElementException());

        assertThat(recommendEvent.getMember().getId()).isEqualTo(registerMemberId);
        assertThat(recommendEvent.getRecommender().getId()).isEqualTo(recommenderId);
    }

    @Test
    void 닉네임_중복_체크() {
        // given
        OauthInfo mockOauthInfo = new OauthInfo("123");
        when(kakaoOauthHelper.getOauthInfoByIdToken(any())).thenReturn(mockOauthInfo);

        MemberProfileDto member1ProfileDto = new MemberProfileDto(MemberProfileType.CHAR, null,
                MemberProfileCharacter.C01, MemberProfileBackground.BLUE);
        MemberRegisterDto member1RegisterDto = new MemberRegisterDto(
                "member1", "asd@gmail.com", "rIdToken", member1ProfileDto,
                true, true, true, true, ""
        );
        memberRegisterService.register(member1RegisterDto);

        // when
        NicknameExistDto member1ExistDto = new NicknameExistDto("member1");
        NicknameExistResDto member1ExistResDto = memberRegisterService.checkNickname(member1ExistDto);
        Boolean member1Exist = member1ExistResDto.getExist();

        NicknameExistDto member2ExistDto = new NicknameExistDto("member2");
        NicknameExistResDto member2ExistResDto = memberRegisterService.checkNickname(member2ExistDto);
        Boolean member2Exist = member2ExistResDto.getExist();

        // then
        assertThat(member1Exist).isTrue();
        assertThat(member2Exist).isFalse();
    }
}