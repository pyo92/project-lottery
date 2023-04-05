package com.example.projectlottery.dto.response.querydsl;

/**
 * 복권판매점검색 페이지 - 검색 가능한 지역 리스팅에 사용되는 지역 정보 response (for QueryDSL projection)
 */
public record QShopRegion(
        String state1,
        String state2,
        String state3,
        Long lottoShopCnt
) {
}