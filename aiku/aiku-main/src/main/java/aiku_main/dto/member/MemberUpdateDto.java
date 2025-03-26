package aiku_main.dto.member;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDto {

    @NotBlank @Size(max = 6)
    public String nickname;
    @Valid
    public MemberProfileDto memberProfileDto;
}
