package common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class Location {

    private String locationName;

    @Column(name = "locationLatitude")
    private Double latitude;

    @Column(name = "locationLongitude")
    private Double longitude;
}
