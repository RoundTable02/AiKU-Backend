package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {

    @NotBlank
    private String locationName;
    @NotNull
    private Double latitude;
    @NotNull
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

    public Location toDomain(){
        return new Location(this.locationName, this.latitude, this.longitude);
    }
}
