package com.example.projectlottery.dto;

import com.example.projectlottery.domain.Shop;

import java.time.LocalDate;

public record ShopDto(
        Long id,
        String address,
        String name,
        String tel,
        double longitude,
        double latitude,
        boolean l645YN,
        boolean l720YN,
        boolean spYN,
        String state1,
        String state2,
        String state3,
        LocalDate scrapedDt
) {

    public static ShopDto of(Long id, String address, String name, String tel, double longitude, double latitude, boolean l645YN, boolean l720YN, boolean spYN, String state1, String state2, String state3, LocalDate scrapedDt) {
        return new ShopDto(id, address, name, tel, longitude, latitude, l645YN, l720YN, spYN, state1, state2, state3, scrapedDt);
    }

    public static ShopDto from(Shop entity) {
        return ShopDto.of(
                entity.getId(),
                entity.getAddress(),
                entity.getName(),
                entity.getTel(),
                entity.getLongitude(),
                entity.getLatitude(),
                entity.isL645YN(),
                entity.isL720YN(),
                entity.isSpYN(),
                entity.getState1(),
                entity.getState2(),
                entity.getState3(),
                entity.getScrapedDt()
        );
    }

    public Shop toEntity() {
        return Shop.of(id, address, name, tel, longitude, latitude, l645YN, l720YN, spYN, state1, state2, state3, scrapedDt);
    }
}
