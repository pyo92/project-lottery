package com.example.projectlottery.dto;

import com.example.projectlottery.domain.Lotto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record LottoDto(
        Long drawNo,
        LocalDate drawDt,
        int number1,
        int number2,
        int number3,
        int number4,
        int number5,
        int number6,
        int numberB
) {

    public static LottoDto of(Long drawNo, LocalDate drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int numberB) {
        return new LottoDto(drawNo, drawDt, number1, number2, number3, number4, number5, number6, numberB);
    }

    public static LottoDto of(Long drawNo, LocalDate drawDt, Set<Integer> numbers, Integer numberB) {
        List<Integer> numberList = numbers.stream().sorted().toList(); //보기 편하게 순서대로 정렬

        return LottoDto.of(
                drawNo,
                drawDt,
                numberList.get(0),
                numberList.get(1),
                numberList.get(2),
                numberList.get(3),
                numberList.get(4),
                numberList.get(5),
                numberB
        );
    }

    public static LottoDto from(Lotto entity) {
        return LottoDto.of(
                entity.getDrawNo(),
                entity.getDrawDt(),
                Set.of(
                   entity.getLottoWinNumber().getNumber1(),
                   entity.getLottoWinNumber().getNumber2(),
                   entity.getLottoWinNumber().getNumber3(),
                   entity.getLottoWinNumber().getNumber4(),
                   entity.getLottoWinNumber().getNumber5(),
                   entity.getLottoWinNumber().getNumber6()
                ),
                entity.getLottoWinNumber().getNumberB()
        );
    }

    public Lotto toEntity() {
        return Lotto.of(drawNo, drawDt, number1, number2, number3, number4, number5, number6, numberB);
    }
}
