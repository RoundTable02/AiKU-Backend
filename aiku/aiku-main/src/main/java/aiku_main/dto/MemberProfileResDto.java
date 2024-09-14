package aiku_main.dto;

import common.domain.member.MemberProfile;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberProfileResDto{
    private MemberProfileType profileType;
    private String profileImg;
    private MemberProfileCharacter profileCharacter;
    private MemberProfileBackground profileBackground;

    public MemberProfileResDto(MemberProfile profile) {
        this.profileType = profile.getProfileType();
        this.profileImg = profile.getProfileImg();
        this.profileCharacter = profile.getProfileCharacter();
        this.profileBackground = profile.getProfileBackground();
    }
}
