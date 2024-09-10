package common.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class Setting {
    //알람 관련 세팅
    private boolean isServicePolicyAgreed;
    private boolean isPersonalInformationPolicyAgreed;
    private boolean isLocationPolicyAgreed;
    private boolean isMarketingPolicyAgreed;
}
