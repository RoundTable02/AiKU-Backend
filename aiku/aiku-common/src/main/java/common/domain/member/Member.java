package common.domain.member;

import common.domain.BaseTime;
import common.domain.ServiceAgreement;
import common.domain.Status;
import common.domain.Title;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

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

    @Enumerated(value = EnumType.STRING)
    private Title mainTitle;
    private int point;

    @Embedded
    private ServiceAgreement serviceAgreement;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void reissueRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    protected Member(
            String email, String nickname, String kakaoId, String password, MemberRole memberRole,
            MemberProfile memberProfile, ServiceAgreement serviceAgreement) {
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = Long.valueOf(kakaoId);
        this.password = password;
        this.role = memberRole;
        this.profile = memberProfile;
        this.serviceAgreement = serviceAgreement;
        this.point = 0;
        this.status = Status.ALIVE;
    }
    public static Member register(
            String email, String nickname,
            String kakaoId, String password,
            MemberProfileType memberProfileType, String profileImg,
            MemberProfileCharacter memberProfileCharacter,
            MemberProfileBackground memberProfileBackground,
            boolean isServicePolicyAgreed,
            boolean isPersonalInformationPolicyAgreed,
            boolean isLocationPolicyAgreed,
            boolean isMarketingPolicyAgreed) {
        MemberProfile memberProfile = new MemberProfile(
                memberProfileType, profileImg,
                memberProfileCharacter, memberProfileBackground);
        ServiceAgreement serviceAgreement = new ServiceAgreement(
                isServicePolicyAgreed,
                isPersonalInformationPolicyAgreed,
                isLocationPolicyAgreed,
                isMarketingPolicyAgreed);

        return new Member(email, nickname, kakaoId, password, MemberRole.MEMBER, memberProfile, serviceAgreement);
    }

    public void updateMember(String nickname, MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter, MemberProfileBackground profileBackground) {
        MemberProfile memberProfile = new MemberProfile(profileType, profileImg, profileCharacter, profileBackground);
        this.nickname = nickname;
        this.profile = memberProfile;
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
