package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.domain.embedded.LottoWinNumber;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Lotto extends AuditingFields {

    @Id
    private Long drawNo; //회차

    @Column
    private LocalDate drawDt; //추첨일

    @Embedded
    private LottoWinNumber lottoWinNumber; //당첨 번호

    @ToString.Exclude
    @OrderBy(value = "rank asc")
    @OneToMany(mappedBy = "lotto")
    private final Set<LottoPrize> lottoPrizes = new LinkedHashSet<>(); //등위별 당첨 금액 정보

    @ToString.Exclude
    @OrderBy(value = "rank asc, no asc")
    @OneToMany(mappedBy = "lotto")
    private final Set<LottoWinShop> lottoWinShops = new LinkedHashSet<>(); //1등, 2등 당첨 판매점 정보

    public static Lotto of(Long drawNo, LocalDate drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int numberB) {
        return new Lotto(
                drawNo,
                drawDt,
                LottoWinNumber.of(number1, number2, number3, number4, number5, number6, numberB)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lotto that)) return false;
        return drawNo.equals(that.drawNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drawNo);
    }
}
