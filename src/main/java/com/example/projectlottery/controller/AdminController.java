package com.example.projectlottery.controller;

import com.example.projectlottery.service.AdminService;
import com.example.projectlottery.service.RedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final AdminService adminService;

    private final RedisTemplateService redisTemplateService;

    @GetMapping
    public String admin(ModelMap map) {
        map.addAttribute("apis", adminService.getAllAPIModifiedAt());
        Map<String, Object> scrapRunningInfo = redisTemplateService.getScrapRunningInfo();
        map.addAttribute("runningYn", scrapRunningInfo.get("url") != null);
        map.addAttribute("runningUrl", scrapRunningInfo.get("url"));
        map.addAttribute("runningParam1", scrapRunningInfo.get("param1"));
        map.addAttribute("runningParam2", scrapRunningInfo.get("param2"));

        return "/user/admin";
    }
}
