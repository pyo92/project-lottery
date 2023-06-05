package com.example.projectlottery.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Spring security filter's exception handler
     * HTTP 401, 403, 404 error 등 예외 발생 시, 해당 핸들러에 전달되며, HomeController 로 redirect 처리한다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendRedirect("/security");
    }
}
