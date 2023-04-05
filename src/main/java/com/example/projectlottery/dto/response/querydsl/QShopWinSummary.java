package com.example.projectlottery.dto.response.querydsl;

/**
 *  복권판매점상세 페이지 - 당첨요약 부분 집계 정보를 전달하기 위한 response (for QueryDSL projection)
 */
public record QShopWinSummary(
        int count1stWin,
        int count1stWinAuto,
        int count1stWinManual,
        long amountSum1stWin,
        long amountMax1stWin,
        long amountMin1stWin,
        int count2ndWin,
        long amountSum2ndWin,
        long amountMax2ndWin,
        long amountMin2ndWin
) {
}
