package common.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class ServiceAgreement {
    //알람 관련 세팅
    private boolean isServicePolicyAgreed;
    private boolean isPersonalInformationPolicyAgreed;
    private boolean isLocationPolicyAgreed;
    private boolean isMarketingPolicyAgreed;

    public static ServiceAgreement makeServiceAgreement(
            boolean isServicePolicyAgreed, boolean isPersonalInformationPolicyAgreed,
            boolean isLocationPolicyAgreed, boolean isMarketingPolicyAgreed) {
        return new ServiceAgreement(isServicePolicyAgreed, isPersonalInformationPolicyAgreed,
                isLocationPolicyAgreed, isMarketingPolicyAgreed);
    }
}
