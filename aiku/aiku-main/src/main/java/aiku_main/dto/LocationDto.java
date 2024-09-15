package aiku_main.dto;

import common.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationDto {
    private String locationName;
    private Double latitude;
    private Double longitude;

    public LocationDto(Location location) {
        this.locationName = location.getLocationName();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
}
