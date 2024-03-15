package com.example.projectlottery.service;

import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.response.APIResponse;
import com.example.projectlottery.dto.response.UserResponse;
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

    private final UserService userService;

    private final AdminRepositoryCustom adminRepository;

    /**
     * Scrap API 목록(+ 최종 업데이트) 전체 조회
     * @return Scrap API 목록(+ 최종 업데이트)
     */
    @Transactional(readOnly = true)
    public List<APIResponse> getAllAPIModifiedAt() {
        return adminRepository.getAllAPIModifiedAt();
    }

    /**
     * 회원 정보 전체 조회
     * @return 회원 목록
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUserList() {
        return userService.getAllUser();
    }

    public void saveUserRoles(String userId, List<String> userRoles) {
        userService.save(
                userId,
                userRoles.stream()
                        .map(UserRoleType::valueOf)
                        .toList()
        );
    }
}
