package common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {

    private String locationName;

    @Column(name = "locationLatitude")
    private Double latitude;

    @Column(name = "locationLongitude")
    private Double longitude;
}
