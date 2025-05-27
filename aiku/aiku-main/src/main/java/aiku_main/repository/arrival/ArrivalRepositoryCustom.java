package aiku_main.repository.arrival;

import common.domain.Arrival;

import java.util.List;

public interface ArrivalRepositoryCustom {

    List<Arrival> findArrivalsOfSchedule(Long scheduleId);
}
