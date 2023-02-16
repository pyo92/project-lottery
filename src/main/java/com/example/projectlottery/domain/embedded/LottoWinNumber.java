package com.example.projectlottery.domain.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class LottoWinNumber {

    @Column(nullable = false)
    private int number1;
    @Column(nullable = false)
    private int number2;
    @Column(nullable = false)
    private int number3;
    @Column(nullable = false)
    private int number4;
    @Column(nullable = false)
    private int number5;
    @Column(nullable = false)
    private int number6;
    @Column(nullable = false)
    private int numberB;

    public static LottoWinNumber of(int number1, int number2, int number3, int number4, int number5, int number6, int numberB) {
        return new LottoWinNumber(number1, number2, number3, number4, number5, number6,numberB);
    }
}
