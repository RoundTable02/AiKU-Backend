package common.domain.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static MemberProfile makeMemberProfile(
            MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter,
            MemberProfileBackground profileBackground) {
        return new MemberProfile(profileType, profileImg, profileCharacter, profileBackground);
    }
}
