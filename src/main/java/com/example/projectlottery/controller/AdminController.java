package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.SeleniumPurchaseService;
import com.example.projectlottery.api.service.SeleniumScrapService;
import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.request.AdminRedisRequest;
import com.example.projectlottery.dto.request.AdminUserRequest;
import com.example.projectlottery.dto.response.RedisResponse;
import com.example.projectlottery.dto.response.SeleniumResponse;
import com.example.projectlottery.dto.response.UserResponse;
import com.example.projectlottery.service.AdminService;
import com.example.projectlottery.service.RedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final AdminService adminService;

    private final SeleniumScrapService seleniumScrapService;
    private final SeleniumPurchaseService seleniumPurchaseService;

    private final RedisTemplateService redisTemplateService;

    @GetMapping
    public String admin(ModelMap map) {
        //API 탭
        map.addAttribute("apis", adminService.getAllAPIModifiedAt());
        Map<String, Object> scrapRunningInfo = redisTemplateService.getScrapRunningInfo();
        map.addAttribute("runningYn", scrapRunningInfo.get("url") != null);
        map.addAttribute("runningUrl", scrapRunningInfo.get("url"));
        map.addAttribute("runningParam1", scrapRunningInfo.get("param1"));
        map.addAttribute("runningParam2", scrapRunningInfo.get("param2"));

        //사용자 탭
        List<UserResponse> users = adminService.getAllUserList();
        map.addAttribute("users", users);
        map.addAttribute("userCnt", users.size());
        map.addAttribute("userRoleTypes", UserRoleType.values());

        return "/user/admin";
    }

    /**
     * 사용자 탭 사용자 권한 정보 저장용 POST method (개별)
     * @param request 사용자 권한 정보 JSON 객체
     * @return 빈 Response entity 객체 (http status)
     */
    @ResponseBody
    @PostMapping("/user")
    public ResponseEntity<?> saveUser(@RequestBody AdminUserRequest request) {
        //TODO: 현재 사용자별 저장이 가능하나, 추후 한 번에 저장하는 기능을 추가하도록 한다.
        try {
            adminService.saveUserRoles(request.userId(), request.userRoles());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }

        return ResponseEntity.ok().body(null);
    }

    //TODO: API 탭 역시 새로고침을 위한 GET method 를 하나 추가해 준다.

    /**
     * Selenium 탭 새로고침용 GET method
     * @return Selenium 정보를 포함한 Response entity 객체
     */
    @ResponseBody
    @GetMapping("/selenium")
    public ResponseEntity<?> getSelenium() {
        try {
            List<SeleniumResponse> result = adminService.getAllSeleniumChromeDriverList();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Selenium chrome driver 중지를 위한 POST method
     * @param usage chrome driver 용도
     * @return 빈 Response entity 객체 (http status)
     */
    @ResponseBody
    @PostMapping("/selenium")
    public ResponseEntity<?> suspendSelenium(String usage) {
        try {
            if (usage.equals("scrap")) {
                //scrap driver 를 멈추고, redis 에서 정보를 삭제한다.
                seleniumScrapService.closeWebDriver();
                redisTemplateService.deleteScrapRunningInfo();

            } else {
                //purchase driver 를 멈추고, redis 에서 정보를 삭제한다.
                seleniumPurchaseService.closeWebDriver();
                redisTemplateService.deletePurchaseWorkerInfo();
            }

            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Redis 탭 새로고침 용 GET method
     * @return Redis 정보를 포함한 Response entity 객체
     */
    @ResponseBody
    @GetMapping("/redis")
    public ResponseEntity<?> getRedis() {
        try {
            List<RedisResponse> results = redisTemplateService.getAllRedisKeyInfo();
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @ResponseBody
    @PostMapping("/redis")
    public ResponseEntity<?> deleteRedisKey(@RequestBody AdminRedisRequest request) {
        try {
            redisTemplateService.deleteByAdmin(
                    request.type(),
                    request.key(),
                    request.field()
            );

            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
