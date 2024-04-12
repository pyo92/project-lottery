package com.example.projectlottery.dto;

public record DhUserInfo(
        String id,
        String name,
        Long deposit,
        Integer purchasableCnt
) {

    public static DhUserInfo of(String id, String name, Long deposit, Integer purchasableCnt) {
        return new DhUserInfo(id, name, deposit, purchasableCnt);
    }
}
