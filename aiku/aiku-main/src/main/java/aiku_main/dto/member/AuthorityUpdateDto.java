package aiku_main.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
