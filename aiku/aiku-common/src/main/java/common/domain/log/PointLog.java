package common.domain.log;

import common.domain.BaseTime;
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

    private Long memberId;

    private int pointAmount;
    private String description;

    private PointLogStatus pointLogStatus;

    protected PointLog(Long memberId, int pointAmount, String description, common.domain.log.PointLogStatus pointLogStatus) {
        this.memberId = memberId;
        this.pointAmount = pointAmount;
        this.description = description;
        this.pointLogStatus = pointLogStatus;
    }
}
