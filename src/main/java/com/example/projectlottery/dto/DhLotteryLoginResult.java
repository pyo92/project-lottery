package com.example.projectlottery.dto;

public record DhLotteryLoginResult(
        Boolean loginYn,
        String errorMsg
) {

    public static DhLotteryLoginResult of(Boolean loginYn, String errorMsg) {
        return new DhLotteryLoginResult(loginYn, errorMsg);
    }

    public static DhLotteryLoginResult of(Boolean loginYn) {
        return DhLotteryLoginResult.of(loginYn, null);
    }
}
