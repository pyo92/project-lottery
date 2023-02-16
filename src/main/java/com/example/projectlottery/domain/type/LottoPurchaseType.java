package com.example.projectlottery.domain.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum LottoPurchaseType {
    AUTO("자동"),
    MANUAL("수동"),
    MIX("반자동"),
    NONE("-");

    @Getter
    private final String description;

    LottoPurchaseType(String description) {
        this.description = description;
    }

    public static LottoPurchaseType getLottoType(String description) {
        Optional<LottoPurchaseType> result = Arrays.stream(LottoPurchaseType.values())
                .filter(lottoPurchaseType -> lottoPurchaseType.getDescription().equals(description))
                .findAny();

        if (result.isEmpty()) {
            return LottoPurchaseType.NONE;
        } else {
            return result.get();
        }
    }
}
