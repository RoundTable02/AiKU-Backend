package aiku_main.dto;

import MemberProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberProfileResDto{
    private String profileType;
    private String profileImg;
    private String profileCharacter;
    private String profileBackground;

    public MemberProfileResDto(MemberProfile profile) {
        this.profileType = profile.getProfileType();
        this.profileImg = profile.getProfileImg();
        this.profileCharacter = profile.getProfileCharacter();
        this.profileBackground = profile.getProfileBackground();
    }
}
