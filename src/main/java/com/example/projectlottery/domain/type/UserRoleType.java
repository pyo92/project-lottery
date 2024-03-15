package com.example.projectlottery.domain.type;

import lombok.Getter;

public enum UserRoleType {
    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자"),
    ROLE_WIN("로또추첨결과"),
    ROLE_SHOP("로또판매점"),
    ROLE_RANKING("로또명당"),
    ROLE_PURCHASE("로또구매"),
    ROLE_COMBINATION("로또조합"),
    ROLE_ANALYSIS("로또분석");

    @Getter
    private final String description;

    UserRoleType(String description) {
        this.description = description;
    }
}
