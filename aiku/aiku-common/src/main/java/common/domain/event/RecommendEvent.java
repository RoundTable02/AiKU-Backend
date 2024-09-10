package common.domain.event;

import common.domain.BaseTime;
import common.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecommendEvent extends BaseTime {

    @Column(name = "recommendEventId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "memberId")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "recommenderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member recommender;
}
