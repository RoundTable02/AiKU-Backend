package common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ShopProduct extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopProductId")
    private Long id;

    private int pointAmount;
    private String productName;
    private String product_img;
    private String productContent;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
