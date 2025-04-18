package aiku_main.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityResDto {
    private boolean isServicePolicyAgreed;
    private boolean isPersonalInformationPolicyAgreed;
    private boolean isLocationPolicyAgreed;
    private boolean isMarketingPolicyAgreed;
}
