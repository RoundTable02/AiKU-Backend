package aiku_main.dto;

import aiku_main.controller.validation.ValidPointAmount;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BettingAddDto {

    @NotNull
    private Long beteeMemberId;
    @Min(10)
    @ValidPointAmount
    private Integer pointAmount;
}
