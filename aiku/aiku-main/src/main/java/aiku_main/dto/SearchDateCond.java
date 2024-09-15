package aiku_main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SearchDateCond {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
