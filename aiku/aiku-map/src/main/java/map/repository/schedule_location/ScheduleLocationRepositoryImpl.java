package map.repository.schedule_location;

import lombok.RequiredArgsConstructor;
import map.dto.RealTimeLocationResDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ScheduleLocationRepositoryImpl implements ScheduleLocationRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int TTL_SECONDS = 60 * 5; // 5분

    // 멤버 위치, 도착여부 저장
    public void saveLocation(Long scheduleId, Long memberId, double latitude, double longitude) {
        String key = "schedule:" + scheduleId + ":" + memberId;

        // 위치 정보 및 도착 여부 저장
        redisTemplate.opsForHash().put(key, "latitude", String.valueOf(latitude));
        redisTemplate.opsForHash().put(key, "longitude", String.valueOf(longitude));
        redisTemplate.opsForHash().put(key, "arrived", String.valueOf(false));

        // 5분 TTL 적용
        redisTemplate.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
    }

    // 해당 스케줄에 해당하는 모든 멤버 위치 조회
    public List<RealTimeLocationResDto> getScheduleLocations(Long scheduleId) {
        Set<String> keys = redisTemplate.keys("schedule:" + scheduleId + ":*");
        if (keys == null || keys.isEmpty()) return Collections.EMPTY_LIST;

        List<RealTimeLocationResDto> locations = new ArrayList<>();
        for (String key : keys) {
            Long userId = Long.parseLong(key.split(":")[2]);
            String latitude = (String) redisTemplate.opsForHash().get(key, "latitude");
            String longitude = (String) redisTemplate.opsForHash().get(key, "longitude");
            String arrivedStr = (String) redisTemplate.opsForHash().get(key, "arrived");

            if (latitude != null && longitude != null) {
                boolean arrived = Boolean.parseBoolean(arrivedStr);
                locations.add(new RealTimeLocationResDto(userId, Double.parseDouble(latitude), Double.parseDouble(longitude), arrived));
            }
        }
        return locations;
    }

    // 멤버 도착 여부 업데이트
    public void updateArrivalStatus(Long scheduleId, Long memberId, boolean arrived) {
        String key = "schedule:" + scheduleId + ":" + memberId;
        redisTemplate.opsForHash().put(key, "arrived", String.valueOf(arrived));

        // TTL 제거, 위치 정보 고정
        redisTemplate.persist(key);
    }

    // 특정 scheduleId에 해당하는 모든 위치 정보 삭제
    public void deleteScheduleLocations(Long scheduleId) {
        Set<String> keys = redisTemplate.keys("schedule:" + scheduleId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

