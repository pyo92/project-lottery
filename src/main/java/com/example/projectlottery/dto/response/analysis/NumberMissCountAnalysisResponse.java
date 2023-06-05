package com.example.projectlottery.dto.response.analysis;

public record NumberMissCountAnalysisResponse(
        Integer number,
        Long count,
        Long bonus
) {

    public static NumberMissCountAnalysisResponse of(Integer number, Long count, Long bonus) {
        return new NumberMissCountAnalysisResponse(number, count, bonus);
    }
}
