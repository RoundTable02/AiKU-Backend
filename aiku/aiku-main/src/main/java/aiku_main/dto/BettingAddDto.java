package aiku_main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BettingAddDto {

    private Long beteeMemberId;
    private int pointAmount;
}
