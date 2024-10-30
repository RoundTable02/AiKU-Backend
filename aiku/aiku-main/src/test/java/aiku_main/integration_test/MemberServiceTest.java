package aiku_main.integration_test;

import aiku_main.dto.*;
import aiku_main.exception.TitleException;
import aiku_main.repository.MemberQueryRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.s3.S3ImageProvider;
import aiku_main.service.MemberService;
import common.domain.member.Member;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import common.domain.title.Title;
import common.domain.title.TitleMember;
import common.domain.value_reference.TitleMemberValue;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    Member member;

    Member member2;

    @BeforeEach
    void init() {
        member = Member.builder()
                .kakaoId(123L)
                .nickname("nickname1")
                .password("password1")
                .email("asdasd@gmail.com")
                .build();

        member.updateProfile(MemberProfileType.CHAR, "", MemberProfileCharacter.C01, MemberProfileBackground.BLUE);

        member.updateAuth(true, true, false, true);

        em.persist(member);

        member2 = Member.builder()
                .kakaoId(124L)
                .nickname("nickname2")
                .password("password2")
                .email("asdasd2@gmail.com")
                .build();

        member2.updateProfile(MemberProfileType.CHAR, "", MemberProfileCharacter.C01, MemberProfileBackground.BLUE);

        member2.updateAuth(true, true, false, true);

        em.persist(member2);
    }

    @Test
    void 멤버_조회_타이틀x_정상() {
        // given
        Title title = Title.builder()
                .titleDescription("타이틀설명")
                .titleImg("타이틀이미지")
                .titleName("타이틀1")
                .build();

        em.persist(title);

        // when
        MemberResDto memberDetail = memberService.getMemberDetail(member.getId());

        // then
        assertThat(memberDetail.getMemberProfile().getProfileType())
                .isEqualTo(MemberProfileType.CHAR);
        assertThat(memberDetail.getPoint())
                .isEqualTo(0);
        assertThat(memberDetail.getNickname())
                .isEqualTo("nickname1");
        assertThat(memberDetail.getKakaoId())
                .isEqualTo(123L);


    }

    @Test
    void 멤버_조회_타이틀o_정상() {
        // given
        Title title = Title.builder()
                .titleDescription("타이틀설명")
                .titleImg("타이틀이미지")
                .titleName("타이틀1")
                .build();

        em.persist(title);

        TitleMember titleMember = TitleMember.giveTitleToMember(member, title);
        em.persist(titleMember);

        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);
        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        // when
        MemberResDto memberDetail = memberService.getMemberDetail(member.getId());

        // then
        assertThat(memberDetail.getMemberProfile().getProfileType())
                .isEqualTo(MemberProfileType.CHAR);
        assertThat(memberDetail.getPoint())
                .isEqualTo(0);
        assertThat(memberDetail.getNickname())
                .isEqualTo("nickname1");
        assertThat(memberDetail.getKakaoId())
                .isEqualTo(123L);
        assertThat(memberDetail.getTitle().getTitleMemberId())
                .isEqualTo(titleMemberValue.getId());
        assertThat(memberDetail.getTitle().getTitleName())
                .isEqualTo("타이틀1");
    }

    @Test
    void 멤버_타이틀_갱신_정상() {
        // given
        Title title = Title.builder()
                .titleDescription("타이틀설명")
                .titleImg("타이틀이미지")
                .titleName("타이틀1")
                .build();
        em.persist(title);

        Title titleNew = Title.builder()
                .titleDescription("타이틀설명2")
                .titleImg("타이틀이미지2")
                .titleName("타이틀2")
                .build();
        em.persist(titleNew);

        TitleMember titleMember = TitleMember.giveTitleToMember(member, title);
        em.persist(titleMember);

        TitleMember titleMemberNew = TitleMember.giveTitleToMember(member, titleNew);
        em.persist(titleMemberNew);

        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);
        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        TitleMemberValue titleMemberValueNew = new TitleMemberValue(titleMemberNew);
        member.updateMemberTitleByTitleMemberId(titleMemberValueNew);

        // when
        memberService.updateTitle(member.getId(), titleMemberValueNew.getId());
        MemberResDto memberDetail = memberService.getMemberDetail(member.getId());

        // then
        assertThat(memberDetail.getTitle().getTitleMemberId())
                .isEqualTo(titleMemberNew.getId());
        assertThat(memberDetail.getTitle().getTitleName())
                .isEqualTo("타이틀2");
    }

    @Test
    void 멤버_타이틀_갱신_보유x_예외() {
        // given
        Title title = Title.builder()
                .titleDescription("타이틀설명")
                .titleImg("타이틀이미지")
                .titleName("타이틀1")
                .build();
        em.persist(title);

        Title titleNew = Title.builder()
                .titleDescription("타이틀설명2")
                .titleImg("타이틀이미지2")
                .titleName("타이틀2")
                .build();
        em.persist(titleNew);

        TitleMember titleMember = TitleMember.giveTitleToMember(member, title);
        em.persist(titleMember);

        TitleMember titleMemberNew = TitleMember.giveTitleToMember(member2, titleNew);
        em.persist(titleMemberNew);

        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);
        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        TitleMemberValue titleMemberValueNew = new TitleMemberValue(titleMemberNew);
        member.updateMemberTitleByTitleMemberId(titleMemberValueNew);

        // when
        // then
        org.junit.jupiter.api.Assertions.assertThrows(
                TitleException.class, () -> {
                    memberService.updateTitle(member.getId(), titleMemberValueNew.getId());
                }
        );
    }

    @Test
    void 멤버_타이틀_리스트_조회() {
        // given
        Title title = Title.builder()
                .titleDescription("타이틀설명")
                .titleImg("타이틀이미지")
                .titleName("타이틀1")
                .build();
        em.persist(title);

        Title titleNew = Title.builder()
                .titleDescription("타이틀설명2")
                .titleImg("타이틀이미지2")
                .titleName("타이틀2")
                .build();
        em.persist(titleNew);

        TitleMember titleMember = TitleMember.giveTitleToMember(member, title);
        em.persist(titleMember);

        TitleMember titleMemberNew = TitleMember.giveTitleToMember(member, titleNew);
        em.persist(titleMemberNew);

        TitleMemberValue titleMemberValue = new TitleMemberValue(titleMember);
        member.updateMemberTitleByTitleMemberId(titleMemberValue);

        TitleMemberValue titleMemberValueNew = new TitleMemberValue(titleMemberNew);
        member.updateMemberTitleByTitleMemberId(titleMemberValueNew);

        // when
        DataResDto<List<TitleMemberResDto>> memberTitles = memberService.getMemberTitles(member.getId());
        List<TitleMemberResDto> resData = memberTitles.getData();

        // then
        assertThat(resData.size())
                .isEqualTo(2);

//        for (TitleMemberResDto resDatum : resData) {
//            System.out.println(resDatum.getTitleDescription());
//        }

    }

    @Test
    void 멤버_권한_조회() {
        AuthorityResDto authDetail = memberService.getAuthDetail(member.getId());
        assertThat(authDetail.isPersonalInformationPolicyAgreed())
                .isTrue();
        assertThat(authDetail.isServicePolicyAgreed())
                .isTrue();
        assertThat(authDetail.isMarketingPolicyAgreed())
                .isTrue();
        assertThat(authDetail.isLocationPolicyAgreed())
                .isFalse();
    }

    @Test
    void 멤버_권한_갱신() {
        AuthorityUpdateDto authorityUpdateDto = new AuthorityUpdateDto(false, true, true, false);
        memberService.updateAuth(member.getId(), authorityUpdateDto);

        AuthorityResDto authDetail = memberService.getAuthDetail(member.getId());
        assertThat(authDetail.isPersonalInformationPolicyAgreed())
                .isTrue();
        assertThat(authDetail.isServicePolicyAgreed())
                .isFalse();
        assertThat(authDetail.isMarketingPolicyAgreed())
                .isFalse();
        assertThat(authDetail.isLocationPolicyAgreed())
                .isTrue();
    }
}