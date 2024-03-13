package com.example.projectlottery.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record APIResponse(
        String url,
        String tableName,
        LocalDateTime modifiedAt
) {

    public static APIResponse of(String url, String tableName, LocalDateTime modifiedAt) {
        return new APIResponse(url, tableName, modifiedAt);
    }

    public String toStringModifiedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return modifiedAt.format(formatter);
    }
}
