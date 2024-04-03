package com.example.projectlottery.dto.response;

public record APIResponse(
        String url,
        String tableName,
        String modifiedAt, //TODO: json 에서 LocalDateTime 타입을 직렬화할 수 있도록 모듈 등록해놨기 떄문에, String 대신 원래 타입 사용하도록 수정
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
