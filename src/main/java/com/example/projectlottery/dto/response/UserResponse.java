package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.User;
import com.example.projectlottery.domain.UserRole;
import com.example.projectlottery.domain.type.UserRoleType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public record UserResponse(
        String userId,
        List<UserRoleType> userRoleTypes,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime modifiedAt
) {

    public static UserResponse of(String userId, List<UserRoleType> userRoleTypes, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new UserResponse(userId, userRoleTypes, createdAt, modifiedAt);
    }

    public static UserResponse from(User entity) {
        return UserResponse.of(
                entity.getUserId(),
                entity.getUserRoles()
                        .stream()
                        .sorted(Comparator.comparing(UserRole::getOrd))
                        .map(r -> UserRoleType.valueOf(r.getName()))
                        .toList(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    public String toStringUserRole() {
        return userRoleTypes.contains(UserRoleType.ROLE_ADMIN) ? "관리자" : "사용자";
    }

    public String toStringCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return createdAt.format(formatter);
    }

    public String toStringModifiedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return modifiedAt.format(formatter);
    }
}
