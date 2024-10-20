package map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RacingResDto {

    private RacerResDto firstRacer;
    private RacerResDto secondRacer;
    private LocalDateTime startedAt;
}
