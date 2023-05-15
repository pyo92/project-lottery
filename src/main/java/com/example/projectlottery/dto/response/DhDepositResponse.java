package com.example.projectlottery.dto.response;

public record DhDepositResponse(
        String accountName,
        String accountNumber,
        Long depositAmount
) {

    public static DhDepositResponse of(String accountName, String accountNumber, Long depositAmount) {
        return new DhDepositResponse(accountName, accountNumber, depositAmount);
    }
}
