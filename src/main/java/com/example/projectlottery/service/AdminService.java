package com.example.projectlottery.service;

import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.response.APIWithRunningInfoResponse;
import com.example.projectlottery.dto.response.SeleniumResponse;
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

    private final RedisTemplateService redisTemplateService;

    private final AdminRepositoryCustom adminRepository;

    /**
     * Scrap API 목록(+ 최종 업데이트) 전체 조회
     * @return Scrap API 목록(+ 최종 업데이트)
     */
    @Transactional(readOnly = true)
    public APIWithRunningInfoResponse getAllAPI() {
        return new APIWithRunningInfoResponse(
                adminRepository.getAllAPIModifiedAt(),
                redisTemplateService.getScrapRunningInfo()
        );
    }

    /**
     * 회원 정보 전체 조회
     * @return 회원 목록
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUserList() {
        return userService.getAllUser();
    }

    /**
     * 회원 정보 저장 (개별)
     * @param userId 대상 user id
     * @param userRoles user role list
     */
    public void saveUserRoles(String userId, List<String> userRoles) {
        userService.save(
                userId,
                userRoles.stream()
                        .map(UserRoleType::valueOf)
                        .toList()
        );
    }

    /**
     * Selenium chrome driver 정보 조회
     * @return Selenium chrome driver 정보
     */
    @Transactional(readOnly = true)
    public List<SeleniumResponse> getAllSeleniumChromeDriverList() {
        List<String> chromeDrivers = adminRepository.getAllSeleniumChromeDriver();

        boolean isScrapRunning = redisTemplateService.getScrapRunningInfo().url() != null;
        boolean isPurchaseRunning = redisTemplateService.getPurchaseWorkerInfo() != null;

        return List.of(
                new SeleniumResponse(chromeDrivers.get(0), isScrapRunning),
                new SeleniumResponse(chromeDrivers.get(1), isPurchaseRunning)
        );
    }
}
