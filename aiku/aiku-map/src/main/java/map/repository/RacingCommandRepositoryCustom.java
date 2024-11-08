package map.repository;

public interface RacingCommandRepositoryCustom {

    void setWinnerAndTermRacingByScheduleMemberId(Long scheduleMemberId);

    void terminateRunningRacing(Long scheduleId);
}
