package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.PurchaseResult;
import com.example.projectlottery.domain.type.LottoPurchaseType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PurchaseResultResponse(
        Long drawNo,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6,
        Integer rank,
        LottoPurchaseType lottoPurchaseType,
        LocalDateTime purchasedAt
) {

    public static PurchaseResultResponse of(Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank, LottoPurchaseType lottoPurchaseType, LocalDateTime purchasedAt) {
        return new PurchaseResultResponse(drawNo, number1, number2, number3, number4, number5, number6, rank, lottoPurchaseType, purchasedAt);
    }

    public static PurchaseResultResponse from(PurchaseResult entity) {
        return PurchaseResultResponse.of(
                entity.getDrawNo(),
                entity.getNumber1(),
                entity.getNumber2(),
                entity.getNumber3(),
                entity.getNumber4(),
                entity.getNumber5(),
                entity.getNumber6(),
                entity.getRank(),
                entity.getPurchaseType(),
                entity.getCreatedAt()
        );
    }

    public String toStringPurchasedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return purchasedAt.format(formatter);
    }
}
