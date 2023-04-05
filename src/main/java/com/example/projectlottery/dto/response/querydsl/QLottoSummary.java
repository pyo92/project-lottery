package com.example.projectlottery.dto.response.querydsl;

/**
 * 복권판매점상세 페이지 - 1, 2등 당첨이력 리스팅에 사용되는 로또 회차별 요약 정보 response (for QueryDSL projection)
 */
public record QLottoSummary(
        Long drawNo,
        Integer no,
        String drawDt,
        int number1,
        int number2,
        int number3,
        int number4,
        int number5,
        int number6,
        int bonus,
        long winAmountPerGame,
        String lottoPurchaseType
) {
}
