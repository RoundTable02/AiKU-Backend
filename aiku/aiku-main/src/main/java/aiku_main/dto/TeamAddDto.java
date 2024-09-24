package aiku_main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamAddDto {
    @NotBlank @Size(max = 15)
    private String groupName;
}
