package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.member.MemberProfile;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
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

    @QueryProjection
    public MemberProfileResDto(MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter, MemberProfileBackground profileBackground) {
        this.profileType = profileType;
        this.profileImg = profileImg;
        this.profileCharacter = profileCharacter;
        this.profileBackground = profileBackground;
    }
}
