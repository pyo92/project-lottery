package com.example.projectlottery.service;

import com.example.projectlottery.dto.response.APIResponse;
import com.example.projectlottery.repository.querydsl.AdminRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepositoryCustom adminRepository;

    @Transactional(readOnly = true)
    public List<APIResponse> getAllAPIModifiedAt() {
        return adminRepository.getAllAPIModifiedAt();
    }
}
