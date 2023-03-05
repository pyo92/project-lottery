package com.example.projectlottery.controller;

import com.example.projectlottery.dto.response.shop.ShopResponse;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public String shop(@RequestParam String shopId, ModelMap map) {
        ShopResponse shopResponse = shopService.getShopResponse(Long.parseLong(shopId));
        map.addAttribute("shopResponse", shopResponse);

        return "/shop/detail";
    }
}
