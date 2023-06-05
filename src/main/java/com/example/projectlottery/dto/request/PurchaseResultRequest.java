package com.example.projectlottery.dto.request;

import com.example.projectlottery.domain.PurchaseResult;
import com.example.projectlottery.domain.type.LottoPurchaseType;

public record PurchaseResultRequest(
        String userId,
        Long drawNo,
        LottoPurchaseType purchaseType,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6
) {

    public static PurchaseResultRequest of(String userId, Long drawNo, LottoPurchaseType purchaseType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return new PurchaseResultRequest(userId, drawNo, purchaseType, number1, number2, number3, number4, number5, number6);
    }

    public PurchaseResult toEntity() {
        return PurchaseResult.from(this);
    }
}
