package com.example.projectlottery.controller;

import com.example.projectlottery.dto.response.shop.ShopResponse;
import com.example.projectlottery.service.PaginationService;
import com.example.projectlottery.service.RegionService;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.TreeSet;


@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
public class ShopController {

    private final ShopService shopService;
    private final RegionService regionService;
    private final PaginationService paginationService;

    @GetMapping
    public String shop(@RequestParam String shopId, ModelMap map) {
        ShopResponse shopResponse = shopService.getShopResponse(Long.parseLong(shopId));
        map.addAttribute("shopResponse", shopResponse);

        return "/shop/detail";
    }

    @GetMapping("/list")
    public String shops(
            @RequestParam(required = false) String state1,
            @RequestParam(required = false) String state2,
            @PageableDefault(sort = "address", direction = Sort.Direction.ASC) Pageable pageable,
            ModelMap map
    ) {
        Page<ShopResponse> shops = shopService.getShopListResponse(state1, state2, pageable);
        List<Integer> pagination = paginationService.getPagination(pageable.getPageNumber(), shops.getTotalPages());

        List<String> state1List = regionService.getAllState1();
        List<String> state2List = regionService.getAllState2(state1);

        map.addAttribute("shops", shops);
        map.addAttribute("pagination", pagination);
        map.addAttribute("state1List", state1List);
        map.addAttribute("state2List", state2List);

        return "/shop/list";
    }


    @GetMapping("/ranking")
    public String ranking(ModelMap map) {
        TreeSet<ShopResponse> ranking = shopService.getShopRankingResponse();

        map.addAttribute("ranking", ranking);

        return "/shop/ranking";
    }
}
