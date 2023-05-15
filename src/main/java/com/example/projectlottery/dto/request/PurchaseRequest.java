package com.example.projectlottery.dto.request;

import com.example.projectlottery.domain.Purchase;

public record PurchaseRequest(
        String userId,
        String userDhLotteryId,
        Long drawNo,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6
) {

    public static PurchaseRequest of(String userId, String userDhLotteryId, Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return new PurchaseRequest(userId, userDhLotteryId, drawNo, number1, number2, number3, number4, number5, number6);
    }

    public static PurchaseRequest of(String userDhLotteryId, Long drawNo, int[] game) {
        return PurchaseRequest.of(
                "", //TODO: 회원가입 기능 만들고 나서 security context 에서 인증 정보를 넣어준다.
                userDhLotteryId,
                drawNo,
                game[0],
                game[1],
                game[2],
                game[3],
                game[4],
                game[5]
        );
    }

    public Purchase toEntity() {
        return Purchase.from(this);
    }
}
