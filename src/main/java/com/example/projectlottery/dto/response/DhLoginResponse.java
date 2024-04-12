package com.example.projectlottery.dto.response;

import com.example.projectlottery.dto.DhLotteryLoginResult;
import com.example.projectlottery.dto.DhUserInfo;

/**
 * 복권 구매를 위한 동행복권 사이트 로그인 response dto
 */
public record DhLoginResponse(
        Boolean loginOk,
        String errorMessage,
        String id,
        String name,
        Long deposit,
        Integer purchasableCount
) {

    public static DhLoginResponse of(Boolean loginOk, String errorMessage, String id, String name, Long deposit, Integer purchasableCount) {
        return new DhLoginResponse(loginOk, errorMessage, id, name, deposit, purchasableCount);
    }

    public static DhLoginResponse of(Boolean loginOk, String errorMessage) {
        return DhLoginResponse.of(loginOk, errorMessage, null, null, null, null);
    }

    public static DhLoginResponse of(DhLotteryLoginResult login, DhUserInfo userInfo) {
        return DhLoginResponse.of(
                login.loginYn(),
                login.errorMsg(),
                userInfo.id(),
                userInfo.name(),
                userInfo.deposit(),
                userInfo.purchasableCnt()
        );
    }
    public static DhLoginResponse of(DhLotteryLoginResult login) { //로그인 실패
        return DhLoginResponse.of(
                login.loginYn(),
                login.errorMsg(),
                null,
                null,
                null,
                null
        );
    }
}
