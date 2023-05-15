package com.example.projectlottery.dto.response;

public record DhLottoPurchaseResponse(
        Boolean purchaseOk,
        String errorMessage
) {

    public static DhLottoPurchaseResponse of(Boolean purchaseOk, String errorMessage) {
        return new DhLottoPurchaseResponse(purchaseOk, errorMessage);
    }
}
