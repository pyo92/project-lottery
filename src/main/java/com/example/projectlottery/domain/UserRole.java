package com.example.projectlottery.domain;

import com.example.projectlottery.domain.type.UserRoleType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserRole {

    @Id
    private String name;

    private String description;

    private Long ord;

    public static UserRole of(String name, String description, Long ordinal) {
        return new UserRole(name, description, ordinal);
    }

    public static UserRole from(UserRoleType userRoleType) {
        return UserRole.of(userRoleType.toString(), userRoleType.getDescription(), (long) userRoleType.ordinal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole that)) return false;
        return this.name != null && this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
