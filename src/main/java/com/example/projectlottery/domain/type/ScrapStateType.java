package com.example.projectlottery.domain.type;

import lombok.Getter;

public enum ScrapStateType {
    ALL("전국"),
    SEOUL("서울특별시"),
    GYEONGGI("경기도"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    GANGWON("강원도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    GWANGJU("광주광역시"),
    JEONBUK("전라북도"),
    JEONNAM("전라남도"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    JEJU("제주특별자치도"),
    SEJONG("세종특별자치시");

    @Getter
    private final String description;

    ScrapStateType(String description) {
        this.description = description;
    }
}
