package aiku_main.dto;

import aiku_main.controller.validation.ValidPointAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEnterDto {
    @ValidPointAmount
    private int pointAmount;
}
