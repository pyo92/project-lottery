package com.example.projectlottery.controller;

import com.example.projectlottery.dto.response.ShopResponse;
import com.example.projectlottery.dto.response.querydsl.QShopRegion;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.service.PaginationService;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
public class ShopController {

    private final ShopService shopService;
    private final PaginationService paginationService;

    @GetMapping("/list")
    public String shops(
            @RequestParam(required = false) String state1,
            @RequestParam(required = false) String state2,
            @RequestParam(required = false) String state3,
            @PageableDefault Pageable pageable,
            ModelMap map
    ) {
        List<QShopRegion> QShopRegionList = shopService.getShopRegionResponse(state1, state2, state3);
        Page<QShopSummary> shops = shopService.getShopListResponse(state1, state2, state3, pageable);
        List<Integer> pagination = paginationService.getPagination(pageable.getPageNumber(), shops.getTotalPages());

        map.addAttribute("regions", QShopRegionList);
        map.addAttribute("shops", shops);
        map.addAttribute("pagination", pagination);

        return "shop/shopList";
    }

    @GetMapping
    public String shop(@RequestParam String shopId, ModelMap map) {
        ShopResponse shopResponse = shopService.getShopResponse(Long.parseLong(shopId));
        map.addAttribute("shopResponse", shopResponse);

        return "shop/shopDetail";
    }


    @GetMapping("/ranking")
    public String ranking(ModelMap map) {
        List<QShopSummary> ranking = shopService.getShopRankingResponse();

        map.addAttribute("ranking", ranking);

        return "shop/shopRanking";
    }
}
