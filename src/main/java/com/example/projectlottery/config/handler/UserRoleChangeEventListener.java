package com.example.projectlottery.config.handler;

import com.example.projectlottery.dto.auth.CustomUserDetails;
import com.example.projectlottery.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRoleChangeEventListener {

    private final UserService userService;

    /**
     * UserRoleChangeEvent listener
     * @param event user role 변경 이벤트
     */
    @EventListener
    public void handleUserRoleChangeEvent(UserRoleChangeEvent event) {
        //이벤트에서 사용자 id 추출
        String userId = event.getUserId();

        //security context 에서 사용자 정보 조회
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        //security context 에서 사용자 id 추출
        String currentUserId = userDetails.getUsername();

        //이벤트와 security context 에서의 id 가 동일한지 체크
        //-> 이벤트가 발행되면 broadcast 되지만, 특정 사용자의 권한만 수정해야 하므로
        if (currentUserId.equals(userId)) {
            //role 변경사항을 반영하기 위해 사용자 정보를 새롭게 조회
            CustomUserDetails currentUserDetails = CustomUserDetails.from(
                    userService.getUser(userId)
                            .orElseThrow(() -> new EntityNotFoundException("=== [ERROR] User not found - " + userId))
            );

            //새롭게 조회한 사용자 정볼르 바탕으로 authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    currentUserDetails, currentUserDetails.getPassword(), currentUserDetails.getAuthorities()
            );

            //security context 업데이트
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
    }
}
