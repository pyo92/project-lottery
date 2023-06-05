package com.example.projectlottery.domain.type;

import lombok.Getter;

public enum UserCombinationType {
    RECOMMEND("추천번호"),
    FIXED("고정수"),
    NEGATIVE("제외수");

    @Getter
    private final String description;

    UserCombinationType(String description) {
        this.description = description;
    }
}
