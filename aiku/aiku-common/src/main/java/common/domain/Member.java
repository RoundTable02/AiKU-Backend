package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Member extends BaseTime{

    @Column(name = "memberId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long kakaoId;
    private String refreshToken;

    private String nickname;
    private String phoneNumber;

    @Embedded
    private MemberProfile profile;

    @Enumerated(value = EnumType.STRING)
    private String mainTitle;
    private int point;

    @Embedded
    private Setting setting;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
