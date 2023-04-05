package com.example.projectlottery.dto.response.querydsl;

/**
 *  복권판매점, 로또추첨결과, 로또명당 페이지 - 모든 페이지에서 복권 판매점을 리스팅하기 위한 판매점 요약 정보 response (for QueryDSL projection)
 */
public record QShopSummary(
        Long id,
        String name,
        String address,
        String lottoPurchaseType,
        Long firstPrizeWinCount,
        Long secondPrizeWinCount
) {
}
