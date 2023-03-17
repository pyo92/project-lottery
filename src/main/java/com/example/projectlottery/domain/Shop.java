package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Shop extends AuditingFields {

    @Id
    private Long id; //판매점 id

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
    private boolean l645YN; //로또6/45 판매 여부
    @Column
    private boolean l720YN; //연금복권720+ 판매 여부
    @Column
    private boolean spYN; //스피또 판매 여부

    @Column
    private String state1; //시.도
    @Column
    private String state2; //시.군.구
    @Column
    private String state3; //읍.면.동.리

    @Column
    private LocalDate scrapedDt; //스크랩핑 일자

    @ToString.Exclude
    @OneToMany(mappedBy = "shop")
    private final Set<LottoWinShop> lottoWinShops = new LinkedHashSet<>(); //1등, 2등 당첨 회차 목록

    public static Shop of(Long id, String address, String name, String tel, double longitude, double latitude, boolean l645YN, boolean l720YN, boolean spYN, String state1, String state2, String state3, LocalDate scrapedDt) {
        return new Shop(id, address, name, tel, longitude, latitude, l645YN, l720YN, spYN, state1, state2, state3, scrapedDt);
    }
}
