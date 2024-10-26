package map.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import map.controller.validation.ValidPointAmount;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RacingAddDto {

    @NotNull
    private Long targetMemberId;

    @Min(10)
    @ValidPointAmount
    private Integer point;
}
