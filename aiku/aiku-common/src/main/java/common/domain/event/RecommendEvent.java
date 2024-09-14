package common.domain.event;

import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "Recommend")
@Entity
public class RecommendEvent extends CommonEvent {

    @JoinColumn(name = "recommenderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member recommender;
}
