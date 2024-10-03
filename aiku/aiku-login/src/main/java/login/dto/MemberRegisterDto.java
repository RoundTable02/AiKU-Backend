package login.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterDto {
    @NotBlank @Size(max = 6)
    private String nickname;
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @NotBlank
    private String idToken;
    @Valid
    private MemberProfileDto memberProfile;
    @NotNull
    private Boolean isServicePolicyAgreed;
    @NotNull
    private Boolean isPersonalInformationPolicyAgreed;
    @NotNull
    private Boolean isLocationPolicyAgreed;
    @NotNull
    private Boolean isMarketingPolicyAgreed;

    private String recommenderNickname;
}
