package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.APIResponse;

import java.util.List;

public interface AdminRepositoryCustom {

    List<APIResponse> getAllAPIModifiedAt();
}
