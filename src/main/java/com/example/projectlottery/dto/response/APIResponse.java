package com.example.projectlottery.dto.response;

public record APIResponse(
        String url,
        String tableName,
        String modifiedAt,
        Long maxDrawNo
) {

    public static APIResponse of(String url, String tableName, String modifiedAt, Long maxDrawNo) {
        return new APIResponse(
                url,
                tableName,
                modifiedAt,
                maxDrawNo
        );
    }
}
