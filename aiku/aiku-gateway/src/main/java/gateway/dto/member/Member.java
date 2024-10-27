package gateway.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member{

    private Long id;
    private Long kakaoId;
    private String refreshToken;

    private String nickname;
    private String email;

    private String password;

    private MemberRole role; //MANAGER, MEMBER
}
