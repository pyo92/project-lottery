package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.request.PurchaseResultRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PurchaseResult extends AuditingFields {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String userId; //사용자 id

    @Column
    private Long drawNo; //회차

    @Enumerated(EnumType.STRING)
    @Column
    private LottoPurchaseType purchaseType; //구매타입

    @Column
    private Integer number1; //번호1
    @Column
    private Integer number2; //번호2
    @Column
    private Integer number3; //번호3
    @Column
    private Integer number4; //번호4
    @Column
    private Integer number5; //번호5
    @Column
    private Integer number6; //번호6

    @Column
    private Integer rank; //등위

    public static PurchaseResult of(Long id, String userId, Long drawNo, LottoPurchaseType purchaseType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank) {
        return new PurchaseResult(id, userId, drawNo, purchaseType, number1, number2, number3, number4, number5, number6, rank);
    }

    public static PurchaseResult of(String userId, Long drawNo, LottoPurchaseType purchaseType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank) {
        return PurchaseResult.of(null, userId, drawNo, purchaseType, number1, number2, number3, number4, number5, number6, rank);
    }

    public static PurchaseResult from(PurchaseResultRequest dto) {
        return PurchaseResult.of(
                dto.userId(),
                dto.drawNo(),
                dto.purchaseType(),
                dto.number1(),
                dto.number2(),
                dto.number3(),
                dto.number4(),
                dto.number5(),
                dto.number6(),
                null //구매 당시에는 추첨하지 않았기 때문에 등위를 알 수 없다.
        );
    }

    public void updateRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseResult that)) return false;
        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
