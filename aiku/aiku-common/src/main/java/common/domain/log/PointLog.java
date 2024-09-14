package common.domain.log;

import common.domain.BaseTime;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorColumn(name = "logType")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class PointLog extends BaseTime {

    @Column(name = "pointLogId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int pointAmount;
    private String description;

    private PointLogStatus PointLogStatus;
}
