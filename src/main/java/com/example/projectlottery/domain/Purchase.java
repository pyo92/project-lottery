package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.dto.request.PurchaseRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Purchase extends AuditingFields {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String userId;

    @Column
    private String userDhLotteryId;

    @Column
    private Long drawNo;

    @Column
    private Integer number1;
    @Column
    private Integer number2;
    @Column
    private Integer number3;
    @Column
    private Integer number4;
    @Column
    private Integer number5;
    @Column
    private Integer number6;

    public static Purchase of(Long id, String userId, String userDhLotteryId, Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return new Purchase(id, userId, userDhLotteryId, drawNo, number1, number2, number3, number4, number5, number6);
    }

    public static Purchase of(String userId, String userDhLotteryId, Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return Purchase.of(null, userId, userDhLotteryId, drawNo, number1, number2, number3, number4, number5, number6);
    }

    public static Purchase from(PurchaseRequest dto) {
        return Purchase.of(
                dto.userId(),
                dto.userDhLotteryId(),
                dto.drawNo(),
                dto.number1(),
                dto.number2(),
                dto.number3(),
                dto.number4(),
                dto.number5(),
                dto.number6()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase that)) return false;
        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
