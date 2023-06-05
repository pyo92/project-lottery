package com.example.projectlottery.dto.response.analysis;

public record NumberSumAnalysisResponse(
        Long drawNo,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6,
        Integer sum
) {

    public static NumberSumAnalysisResponse of(Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer sum) {
        return new NumberSumAnalysisResponse(drawNo, number1, number2, number3, number4, number5, number6, sum);
    }
}
