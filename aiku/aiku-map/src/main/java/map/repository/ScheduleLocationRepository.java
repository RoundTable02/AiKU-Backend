package map.repository;

import map.dto.RealTimeLocationResDto;

import java.util.List;

public interface ScheduleLocationRepository {

    // 멤버 위치, 도착여부 저장
    void saveLocation(Long scheduleId, Long memberId, double latitude, double longitude);

    // 해당 스케줄에 해당하는 모든 멤버 위치 조회
    List<RealTimeLocationResDto> getScheduleLocations(Long scheduleId);

    // 멤버 도착 여부 업데이트
    void updateArrivalStatus(Long scheduleId, Long memberId, boolean arrived);

    // 특정 scheduleId에 해당하는 모든 위치 정보 삭제
    void deleteScheduleLocations(Long scheduleId);

}
