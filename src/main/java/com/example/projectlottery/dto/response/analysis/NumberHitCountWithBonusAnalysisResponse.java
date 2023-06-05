package com.example.projectlottery.dto.response.analysis;

public record NumberHitCountWithBonusAnalysisResponse(
        Integer number,
        Long count,
        Long bonus
) {

    public static NumberHitCountWithBonusAnalysisResponse of(Integer number, Long count, Long bonus) {
        return new NumberHitCountWithBonusAnalysisResponse(number, count, bonus);
    }
}
