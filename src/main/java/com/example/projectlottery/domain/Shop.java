package com.example.projectlottery.domain;

import com.example.projectlottery.domain.embedded.ShopRegion;
import com.example.projectlottery.domain.embedded.ShopItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Shop {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String address; //주소
    @Column
    private String name; //상호명
    @Column
    private String tel; //전화번호

    @Column
    private double longitude; //x좌표
    @Column
    private double latitude; //y좌표

    @Column
    private boolean useYN; //사용 여부

    @Embedded
    private ShopRegion shopRegion;
    @Embedded
    private ShopItem shopItem;

    @ToString.Exclude
    @OneToMany(mappedBy = "shop")
    private final Set<LottoWinShop> lottoWinShops = new LinkedHashSet<>(); //1등, 2등 당첨 회차 목록

    public static Shop of(Long id, String address, String name, String tel, double longitude, double latitude, boolean useYN, String state1, String state2, String state3, boolean l645YN, boolean l720YN, boolean spYN) {
        return new Shop(
                id,
                address,
                name,
                tel,
                longitude,
                latitude,
                useYN,
                ShopRegion.of(state1, state2, state3),
                ShopItem.of(l645YN, l720YN, spYN)
        );
    }
}
