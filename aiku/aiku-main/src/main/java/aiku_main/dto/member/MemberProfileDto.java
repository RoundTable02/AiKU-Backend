package aiku_main.dto.member;

import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileDto {
    @NotNull
    private MemberProfileType profileType;
    private MultipartFile profileImg;
    private MemberProfileCharacter profileCharacter;
    private MemberProfileBackground profileBackground;
}
