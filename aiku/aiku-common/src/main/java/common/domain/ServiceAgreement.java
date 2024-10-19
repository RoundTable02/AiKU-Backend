package common.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class ServiceAgreement {
    // 약관 동의 여부 저장
    private boolean isServicePolicyAgreed;
    private boolean isPersonalInformationPolicyAgreed;
    private boolean isLocationPolicyAgreed;
    private boolean isMarketingPolicyAgreed;
}
