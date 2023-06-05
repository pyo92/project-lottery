package com.example.projectlottery.dto.response.analysis;

public record NumberRangeHitCountAnalysisResponse(
        Integer numberRange,
        Long count,
        Long bonus
) {

    public static NumberRangeHitCountAnalysisResponse of(Integer numberRange, Long count, Long bonus) {
        return new NumberRangeHitCountAnalysisResponse(numberRange, count, bonus);
    }
}
