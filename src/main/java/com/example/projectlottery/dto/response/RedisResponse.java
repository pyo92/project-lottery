package com.example.projectlottery.dto.response;

import org.springframework.data.redis.connection.DataType;

import java.util.List;

public record RedisResponse(
        String key,
        DataType type,
        Object value, //set 만 사용하는 속성 (실제 값)
        Long zSetCnt, //sorted set 만 사용하는 속성 (내부 항목 개수)
        List<Object> hSetFields //hash set 만 사용하는 속성 (내부 필드 key 값 목록)
) {

}
