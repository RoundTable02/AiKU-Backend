package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LocationDto {
    private String locationName;
    private Double latitude;
    private Double longitude;

    public LocationDto(Location location) {
        this.locationName = location.getLocationName();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @QueryProjection
    public LocationDto(String locationName, Double latitude, Double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
