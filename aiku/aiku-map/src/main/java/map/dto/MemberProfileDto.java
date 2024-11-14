package map.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class MemberProfileDto {

    private MemberProfileType profileType;
    private String profileImg;
    private MemberProfileCharacter profileCharacter;
    private MemberProfileBackground profileBackground;

    @QueryProjection
    public MemberProfileDto(MemberProfileType profileType, String profileImg, MemberProfileCharacter profileCharacter, MemberProfileBackground profileBackground) {
        this.profileType = profileType;
        this.profileImg = profileImg;
        this.profileCharacter = profileCharacter;
        this.profileBackground = profileBackground;
    }
}
