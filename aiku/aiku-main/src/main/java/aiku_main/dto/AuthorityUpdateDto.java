package aiku_main.dto;

import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityUpdateDto {
    @NotNull
    private boolean isServicePolicyAgreed;
    @NotNull
    private boolean isPersonalInformationPolicyAgreed;
    @NotNull
    private boolean isLocationPolicyAgreed;
    @NotNull
    private boolean isMarketingPolicyAgreed;
}
