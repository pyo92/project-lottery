package com.example.projectlottery.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Spring security filter's exception handler
     * HTTP 404 error 같이 올바르지 않은 경로 요청 시, 해당 핸들러에 전달되며, SecurityController (auth) 로 redirect 처리한다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendRedirect("/security/auth");
    }
}
