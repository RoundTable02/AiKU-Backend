package aiku_main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BettingAddDto {
    private Long beteeMemberId;
    private int pointAmount;
}
