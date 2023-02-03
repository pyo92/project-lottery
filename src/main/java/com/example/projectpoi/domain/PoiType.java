package com.example.projectpoi.domain;

import lombok.Getter;

public enum PoiType {
    LOTTERY("복권판매점");

    @Getter
    private final String description;

    PoiType(String description) {
        this.description = description;
    }
}
