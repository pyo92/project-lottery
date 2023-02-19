package com.example.projectlottery.domain.type;

import lombok.Getter;

public enum ScrapStateType {
    ALL("전체"),
    SEOUL("서울"),
    GYEONGGI("경기"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    DAEJEON("대전"),
    ULSAN("울산"),
    GANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    GWANGJU("광주"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주"),
    SEJONG("세종");

    @Getter
    private final String description;

    ScrapStateType(String description) {
        this.description = description;
    }
}
