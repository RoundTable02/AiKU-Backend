package common.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class ServiceAgreement {
    //알람 관련 세팅
    private boolean isServicePolicyAgreed;
    private boolean isPersonalInformationPolicyAgreed;
    private boolean isLocationPolicyAgreed;
    private boolean isMarketingPolicyAgreed;

}
