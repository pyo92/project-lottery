package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.type.LottoPurchaseType;

import java.util.List;
import java.util.Map;

public record DhLottoPurchaseResponse(
        Boolean purchaseOk,
        String errorMessage,
        Map<String, LottoPurchaseType> purchaseTypes,
        Map<String, List<Integer>> games
) {

    public static DhLottoPurchaseResponse of(Boolean purchaseOk, String errorMessage, Map<String, LottoPurchaseType> purchaseTypes, Map<String, List<Integer>> games) {
        return new DhLottoPurchaseResponse(purchaseOk, errorMessage, purchaseTypes, games);
    }

    public static DhLottoPurchaseResponse of(Boolean purchaseOk, String errorMessage) {
        return DhLottoPurchaseResponse.of(purchaseOk, errorMessage, null, null);
    }
}
