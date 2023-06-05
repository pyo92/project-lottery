package com.example.projectlottery.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.oauth.logout.redirection.url}")
    private String kakaoLogoutRedirectionUrl;

    /**
     * kakao oauth logout
     * @return kakao oauth logout & redirection url
     */
    @GetMapping("/user/logout")
    public String logout() {
        //kakao oauth logout 처리
        return "redirect:https://kauth.kakao.com/oauth/logout?client_id=" + kakaoRestApiKey + "&logout_redirect_uri=" + kakaoLogoutRedirectionUrl;
    }

    /**
     * kakao oauth logout 완료 후, redirection url
     * @param session http session
     * @return spring security context holder clear url
     */
    @GetMapping("/logout/oauth2/code/kakao")
    public String oAuthLogout(HttpSession session) {
        //kakao oauth logout 처리 완료 후, redirection url 로 인해 여기로 전달된다.
        //index 페이지로 redirection 하기 전, spring security 인증 정보를 모두 삭제해줘야 한다.
        //인증 정보를 모두 삭제하기 위한 플래그를 session 에 담아 중간 단계의 redirection url 에 전달한다.
        session.setAttribute("kakaoOAuthLogout", true);

        return "redirect:/logout/oauth2/code/kakao/done";
    }

    /**
     * kakao oauth logout 완료 후, spring security 인증 정보 삭제
     * @param request http servlet request
     * @param response http servlet response
     * @param session http session
     * @return index view file name
     */
    @GetMapping("/logout/oauth2/code/kakao/done")
    public String oAuthLogoutDone(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Boolean kakaoOAuthLogout = (Boolean) session.getAttribute("kakaoOAuthLogout");

        if (kakaoOAuthLogout != null && kakaoOAuthLogout) {
            //JSESSIONID 쿠키 삭제
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        break;
                    }
                }
            }

            //security context holder clear
            SecurityContextHolder.clearContext();
        }

        return "redirect:/";
    }
}
