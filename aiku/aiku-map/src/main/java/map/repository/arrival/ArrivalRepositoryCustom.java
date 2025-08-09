package map.repository.arrival;

public interface ArrivalRepositoryCustom {
    // scheduleId로 schedule과 scheduleMember와 Arrival을 조인하여 전체 개수를 비교
    boolean isAllMembersInScheduleArrived(Long scheduleId);
}
