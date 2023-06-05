package com.example.projectlottery.domain.type;

import lombok.Getter;

public enum UserRoleType {
    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자");

    @Getter
    private final String description;

    UserRoleType(String description) {
        this.description = description;
    }
}
