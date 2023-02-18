package com.example.projectlottery.domain;

import com.example.projectlottery.domain.id.LottoWinShopId;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(LottoWinShopId.class)
@Entity
public class LottoWinShop {

    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    private Lotto lotto; //회차
    @Id
    private Integer rank; //등위
    @Id
    private Integer no; //순번

    @ManyToOne(fetch = FetchType.LAZY)
    private Shop shop; //판매점

    @Enumerated(EnumType.STRING)
    @Column
    private LottoPurchaseType lottoPurchaseType; //구매방식

    @Column
    private String displayAddress; //추첨 당시 가게 주소를 그대로 표시 (동행복권과 동일하게)

    public static LottoWinShop of(Lotto lotto, Integer rank, Integer no, Shop shop, LottoPurchaseType lottoPurchaseType, String displayAddress) {
        return new LottoWinShop(lotto, rank, no, shop, lottoPurchaseType, displayAddress);
    }
}
