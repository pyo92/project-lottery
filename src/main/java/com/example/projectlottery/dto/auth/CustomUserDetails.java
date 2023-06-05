package com.example.projectlottery.dto.auth;

import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.response.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Spring security - 사용자 인증 정보 관련 dto
 */
public record CustomUserDetails(
        String userId,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> oAuth2Attribute
) implements UserDetails, OAuth2User {

    public static CustomUserDetails of(String userId, UserRoleType userRoleType) {
        return new CustomUserDetails(
                userId,
                List.of(new SimpleGrantedAuthority(userRoleType.toString())),
                Map.of());
    }

    public static CustomUserDetails from(UserResponse dto) {
        return CustomUserDetails.of(
                dto.userId(),
                dto.userRoleType()
        );
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attribute;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
