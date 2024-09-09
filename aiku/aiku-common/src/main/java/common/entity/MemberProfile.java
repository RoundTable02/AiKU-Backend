package common.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class MemberProfile {
    private String profileType;
    private String profileImg;
    private String profileCharacter;
    private String profileBackground;
}
