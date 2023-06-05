package com.example.projectlottery.dto.response.analysis;

public record NumberHitCountAnalysisResponse(
        Integer number,
        Long count
) {

    public static NumberHitCountAnalysisResponse of(Integer number, Long count) {
        return new NumberHitCountAnalysisResponse(number, count);
    }
}

