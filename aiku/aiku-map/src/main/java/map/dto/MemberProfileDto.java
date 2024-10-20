package map.dto;

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
@AllArgsConstructor
public class MemberProfileDto {

    private MemberProfileType profileType;
    private MultipartFile profileImg;
    private MemberProfileCharacter profileCharacter;
    private MemberProfileBackground profileBackground;
}
