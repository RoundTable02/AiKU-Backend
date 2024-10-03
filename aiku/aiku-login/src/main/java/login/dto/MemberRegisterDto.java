package login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterDto {
    private String nickname;
    private String email;
    private String idToken;
    private MemberProfileDto memberProfile;
    private Boolean isServicePolicyAgreed;
    private Boolean isPersonalInformationPolicyAgreed;
    private Boolean isLocationPolicyAgreed;
    private Boolean isMarketingPolicyAgreed;
    private String recommenderNickname;
}
