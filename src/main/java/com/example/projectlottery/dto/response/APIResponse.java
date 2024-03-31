package com.example.projectlottery.dto.response;

public record APIResponse(
        String url,
        String tableName,
        String modifiedAt
) {

    public static APIResponse of(String url, String tableName, String modifiedAt) {
        return new APIResponse(
                url,
                tableName,
                modifiedAt
        );
    }
}
