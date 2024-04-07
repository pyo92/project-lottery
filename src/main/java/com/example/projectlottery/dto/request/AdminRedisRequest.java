package com.example.projectlottery.dto.request;

import org.springframework.data.redis.connection.DataType;

public record AdminRedisRequest(
        DataType type,
        String key,
        Object field //hash set 에서 세부 field 삭제시에만 사용
) {
}
