package com.example.projectlottery.dto.auth;

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

    public static CustomUserDetails of(String userId, List<SimpleGrantedAuthority> userAuthorities) {
        return new CustomUserDetails(
                userId,
                userAuthorities,
                Map.of());
    }

    public static CustomUserDetails from(UserResponse dto) {
        return CustomUserDetails.of(
                dto.userId(),
                //userRoleType Enum
                // -> [ROLE_%s] string 만 추출해 SimpleGrantedAuthority 리스트로 만들어 넘긴다.
                dto.userRoleTypes()
                        .stream()
                        .map(Enum::toString)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
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
