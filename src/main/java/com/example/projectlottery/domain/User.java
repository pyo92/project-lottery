package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.domain.type.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends AuditingFields {

    @Id
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRoleType userRoleType;

    public static User of(String userId, UserRoleType userRoleType) {
        return new User(userId, userRoleType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return this.userId != null && this.userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId);
    }
}
