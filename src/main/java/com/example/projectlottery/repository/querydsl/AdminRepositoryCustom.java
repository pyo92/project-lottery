package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.APIResponse;

import java.util.List;

public interface AdminRepositoryCustom {

    /**
     * Scrap api 목록과 영향도 있는 테이블의 최종 업데이트 일시 조회 - 관리자용
     * @return Scrap api 목록 (+ 최종 업데이트 일시)
     */
    List<APIResponse> getAllAPIModifiedAt();

    /**
     * Selenium chrome driver 목록 조회 - 관리자용
     * @return Selenium chrome driver 목록
     */
    List<String> getAllSeleniumChromeDriver();
}
