package com.example.projectlottery.dto.request;

public record DhLottoPurchaseRequest(
        Long drawNo,
        String game1,
        String game2,
        String game3,
        String game4,
        String game5
) {

    public static DhLottoPurchaseRequest of(Long drawNo, String game1, String game2, String game3, String game4, String game5) {
        return new DhLottoPurchaseRequest(drawNo, game1, game2, game3, game4, game5);
    }
}
