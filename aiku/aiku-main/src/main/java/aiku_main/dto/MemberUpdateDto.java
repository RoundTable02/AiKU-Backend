package aiku_main.dto;

import aiku_main.controller.validation.ValidScheduleTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDto {

    @NotBlank @Size(max = 6)
    public String nickname;
    @Valid
    public MemberProfileDto memberProfileDto;
}
