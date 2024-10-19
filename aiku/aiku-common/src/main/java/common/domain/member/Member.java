package common.domain.member;

import common.domain.BaseTime;
import common.domain.ServiceAgreement;
import common.domain.Status;
import common.domain.title.TitleMember;
import common.domain.value_reference.TitleMemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Member extends BaseTime {

    @Column(name = "memberId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long kakaoId;
    private String refreshToken;

    private String nickname;
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role; //MANAGER, MEMBER

    @Embedded
    private MemberProfile profile;

    @AttributeOverride(name = "id", column = @Column(name = "mainTitleId"))
    @Embedded
    private TitleMemberValue mainTitle;

    private int point;

    @Embedded
    private ServiceAgreement serviceAgreement;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public void reissueRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter, MemberProfileBackground profileBackground) {
        MemberProfile memberProfile = MemberProfile.builder()
                .profileType(profileType)
                .profileImg(profileImg)
                .profileCharacter(profileCharacter)
                .profileBackground(profileBackground)
                .build();

        this.profile = memberProfile;
    }

    public void updateAuth(boolean isServicePolicyAgreed, boolean isPersonalInformationPolicyAgreed,
                           boolean isLocationPolicyAgreed, boolean isMarketingPolicyAgreed) {
        ServiceAgreement serviceAgreement = ServiceAgreement.builder()
                .isServicePolicyAgreed(isServicePolicyAgreed)
                .isPersonalInformationPolicyAgreed(isPersonalInformationPolicyAgreed)
                .isLocationPolicyAgreed(isLocationPolicyAgreed)
                .isMarketingPolicyAgreed(isMarketingPolicyAgreed)
                .build();

        this.serviceAgreement = serviceAgreement;
    }

    public void updateMember(String nickname, MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter, MemberProfileBackground profileBackground) {
        updateProfile(profileType, profileImg, profileCharacter, profileBackground);
        this.nickname = nickname;
    }

    @Builder
    public Member(Long kakaoId, String nickname, String password, String email) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = MemberRole.MEMBER;
        this.point = 0;
        this.status = Status.ALIVE;
    }

    public void updateMemberTitleByTitleMemberId(TitleMemberValue titleMemberValue) {
        this.mainTitle = titleMemberValue;
    }

    public void logout() {
        this.refreshToken = null;
    }

    //TODO 후에 수정 or 삭제하세요. TeamService 테스트를 위해 생성 메서드 만들어 둡니다.

    public Member(String nickname) {
        this.nickname = nickname;
        this.profile = new MemberProfile(MemberProfileType.CHAR, "1", MemberProfileCharacter.C01, MemberProfileBackground.BLUE);
        this.status = Status.ALIVE;
    }
    public static Member create(String nickname){
        return new Member(nickname);
    }
    //여기까지
}
