package common.domain.member;

import common.domain.BaseTime;
import common.domain.Setting;
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
    private Setting setting;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void reissueRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
