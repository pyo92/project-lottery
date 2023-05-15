package com.example.projectlottery.dto.request;

/**
 * 복권 구매를 위한 동행복권 사이트 로그인 request dto
 */
public record DhLoginRequest(
        String id,
        String password
) {

    public static DhLoginRequest of(String id, String password) {
        return new DhLoginRequest(id, password);
    }
}
