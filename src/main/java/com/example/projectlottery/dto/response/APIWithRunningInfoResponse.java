package com.example.projectlottery.dto.response;

import java.util.List;

public record APIWithRunningInfoResponse(
        List<APIResponse> apis, //현존하는 api 목록
        APIRunningInfoResponse runningInfo // 현재 실행중인 api 정보
) {
}
