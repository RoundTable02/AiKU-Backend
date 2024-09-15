package aiku_main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDateCond {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
