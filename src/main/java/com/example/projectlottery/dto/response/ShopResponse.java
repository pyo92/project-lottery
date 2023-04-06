package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.Shop;
import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;

import java.util.List;

/**
 *  복권판매점상세 페이지에 포함되는 모든 정보를 담은 response (for redis cache too)
 */
public record ShopResponse(
        Long id,
        String name,
        String address,
        String tel,
        double longitude,
        double latitude,
        boolean l645YN,
        boolean l720YN,
        boolean spYN,
        long count1stWin,
        long count1stWinAuto,
        long count1stWinManual,
        long amountSum1stWin,
        long amountMax1stWin,
        long amountMin1stWin,
        long count2ndWin,
        long amountSum2ndWin,
        long amountMax2ndWin,
        long amountMin2ndWin,
        List<QLottoSummary> lotto1stWin,
        List<QLottoSummary> lotto2ndWin
) {

    public static ShopResponse of(Long id, String name, String address, String tel, double longitude, double latitude, boolean l645YN, boolean l720YN, boolean spYN,
                                  long count1stWin, long count1stWinAuto, long count1stWinManual, long amountSum1stWin, long amountMax1stWin, long amountMin1stWin,
                                  long count2ndWin, long amountSum2ndWin, long amountMax2ndWin, long amountMin2ndWin, List<QLottoSummary> lotto1stWin, List<QLottoSummary> lotto2ndWin) {
        return new ShopResponse(id, name, address, tel, longitude, latitude, l645YN, l720YN, spYN,
                count1stWin, count1stWinAuto, count1stWinManual, amountSum1stWin, amountMax1stWin, amountMin1stWin,
                count2ndWin, amountSum2ndWin, amountMax2ndWin, amountMin2ndWin, lotto1stWin, lotto2ndWin);
    }

    public static ShopResponse from(Shop entity, QShopWinSummary QShopWinSummary, List<QLottoSummary> winSummary1, List<QLottoSummary> winSummary2) {
        return ShopResponse.of(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getTel(),
                entity.getLongitude(),
                entity.getLatitude(),
                entity.isL645YN(),
                entity.isL720YN(),
                entity.isSpYN(),
                QShopWinSummary.count1stWin(),
                QShopWinSummary.count1stWinAuto(),
                QShopWinSummary.count1stWinManual(),
                QShopWinSummary.amountSum1stWin(),
                QShopWinSummary.amountMax1stWin(),
                QShopWinSummary.amountMin1stWin(),
                QShopWinSummary.count2ndWin(),
                QShopWinSummary.amountSum2ndWin(),
                QShopWinSummary.amountMax2ndWin(),
                QShopWinSummary.amountMin2ndWin(),
                winSummary1,
                winSummary2
        );
    }
}
