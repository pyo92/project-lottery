package com.example.projectlottery.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Spring security filter's exception handler
     * HTTP 403 error 같이 권한이 불충분한 요청 시, 해당 핸들러에 전달되며, SecurityController (denied) 로 redirect 처리한다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect("/security/denied");
    }
}
