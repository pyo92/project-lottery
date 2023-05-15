package com.example.projectlottery.dto.request;

public record LottoGameRequest(
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6
) {

    public static LottoGameRequest of(Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return new LottoGameRequest(number1, number2, number3, number4, number5, number6);
    }
}
