package com.example.projectlottery.dto.response.analysis;

public record NumberAcAnalysisResponse(
        Long drawNo,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6,
        Integer ac
) {

    public static NumberAcAnalysisResponse of(Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer ac) {
        return new NumberAcAnalysisResponse(drawNo, number1, number2, number3, number4, number5, number6, ac);
    }
}
