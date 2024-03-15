package com.example.projectlottery.controller;

import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.request.AdminUserRequest;
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
}
