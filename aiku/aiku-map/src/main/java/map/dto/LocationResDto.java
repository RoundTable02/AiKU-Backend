package map.dto;

import common.domain.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationResDto {

    private String locationName;
    private Double latitude;
    private Double longitude;

    public LocationResDto(Location location) {
        this.locationName = location.getLocationName();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
}
