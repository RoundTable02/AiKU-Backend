package gateway.domain.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class MemberProfile {

    @Enumerated(EnumType.STRING)
    private MemberProfileType profileType;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private MemberProfileCharacter profileCharacter;

    @Enumerated(EnumType.STRING)
    private MemberProfileBackground profileBackground;

}
