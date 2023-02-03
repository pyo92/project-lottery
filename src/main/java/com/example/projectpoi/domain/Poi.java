package com.example.projectpoi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(columnList = "poiType")
        }
)
@Entity
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING) //ORDINAL 옵션은 순서가 바뀔 경우 문제가 발생하므로, STRING 옵션 적용
    private PoiType poiType;

    @Column
    private String address;

    @Column
    private String name;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poi poi)) return false;
        return id.equals(poi.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
