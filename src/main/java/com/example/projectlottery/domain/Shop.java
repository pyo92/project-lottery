package com.example.projectlottery.domain;

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
                @Index(columnList = "sido"),
                @Index(columnList = "sigungu"),
                @Index(columnList = "name"),
                @Index(columnList = "longitude"),
                @Index(columnList = "latitude")
        }
)
@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sido;

    @Column
    private String sigungu;

    @Column
    private String roadAddress;

    @Column
    private String address;

    @Column
    private String name;

    @Column
    private Double longitude; //x

    @Column
    private Double latitude; //y

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shop shop)) return false;
        return id != null && id.equals(shop.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
