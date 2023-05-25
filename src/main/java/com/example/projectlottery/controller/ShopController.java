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

    /**
     * 로또판매점 검색 view
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @param keyword 상호
     * @param pageable pageable
     * @param map thymeleaf binding model map
     * @return 로또판매점 검색 view file name
     */
    @GetMapping("/list")
    public String shops(
            @RequestParam(required = false) String state1,
            @RequestParam(required = false) String state2,
            @RequestParam(required = false) String state3,
            @RequestParam(required = false) String keyword,
            @PageableDefault Pageable pageable,
            ModelMap map
    ) {
        //상당 행정구역 목록 및 판매점 수 조회
        List<QShopRegion> QShopRegionList = shopService.getShopRegionResponse(state1, state2, state3);

        //행정구역에 속하고 keyword 가 상호에 포함된 판매점 목록 조회
        Page<QShopSummary> shops = shopService.getShopListResponse(state1, state2, state3, keyword, pageable);

        //하단 pagination
        List<Integer> pagination = paginationService.getPagination(pageable.getPageNumber(), shops.getTotalPages());

        map.addAttribute("regions", QShopRegionList);
        map.addAttribute("shops", shops);
        map.addAttribute("pagination", pagination);

        return "shop/shopList";
    }

    /**
     * 로또판매점 상세 view
     * @param shopId 판매점 id
     * @param map thymeleaf binding model map
     * @return 로또판매점 상세 view file name
     */
    @GetMapping
    public String shop(@RequestParam String shopId, ModelMap map) {
        ShopResponse shopResponse = shopService.getShopResponse(Long.parseLong(shopId));

        map.addAttribute("shopResponse", shopResponse);

        return "shop/shopDetail";
    }

    /**
     * 로또 명당 view
     * @param map thymeleaf binding model map
     * @return 로또 명당 view file name
     */
    @GetMapping("/ranking")
    public String ranking(ModelMap map) {
        List<QShopSummary> ranking = shopService.getShopRankingResponse();

        //리스트를 4개의 조각으로 나눠서 map binding
        map.addAttribute("chucked1", ranking.subList(0, 10));
        map.addAttribute("chucked2", ranking.subList(10, 30));
        map.addAttribute("chucked3", ranking.subList(30, 50));
        map.addAttribute("chucked4", ranking.subList(50, 100));

        return "shop/shopRanking";
    }
}
