package map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RunningRacingDto {

    private Long racingId;
    private Long firstScheduleMemberId;
    private Long secondScheduleMemberId;
    private Integer pointAmount;
}
