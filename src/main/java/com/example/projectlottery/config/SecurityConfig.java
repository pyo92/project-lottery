package com.example.projectlottery.config;

import com.example.projectlottery.config.handler.CustomAccessDeniedHandler;
import com.example.projectlottery.config.handler.CustomAuthenticationEntryPoint;
import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.auth.CustomUserDetails;
import com.example.projectlottery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    /**
     * Spring security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return httpSecurity
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authorizeHttpRequests(auth -> {
                            try {
                                auth
                                        //static resources 에 대한 접근 허용
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .requestMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**").permitAll()
                                        //루트, 보안 페이지에 대한 접근 허용
                                        .requestMatchers("/", "/security/auth", "/security/denied").permitAll()
                                        //간편 로그인 페이지에 대한 접근 허용
                                        .requestMatchers("/oauth2/authorization/kakao").permitAll()
                                        //scrap rest api 에 대한 접근 관리자만 허용
                                        .requestMatchers("/scrap/**").hasRole("ADMIN")
                                        .anyRequest().authenticated()
                                        //security 예외 핸들러를 등록해 예외 처리
                                        .and()
                                        .exceptionHandling()
                                        //비인가 사용자에 대한 핸들러 처리
                                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                        //사용자에 권한에 대한 핸들러 처리
                                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                                        .and()
                                        //oAuth login 핸들러 등록
                                        .oauth2Login(oAuth -> oAuth
                                                .userInfoEndpoint(userInfo -> userInfo
                                                        .userService(oAuth2UserService)));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .build();
    }

    /**
     * oauth user service handler
     * @param userService user service
     * @return oauth user service
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(UserService userService) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            //id, role 바인딩
            String oAuthId = "k_" + oAuth2User.getName();
            UserRoleType userRoleType = oAuth2User.getName().equals("2811950709") ? UserRoleType.ROLE_ADMIN : UserRoleType.ROLE_USER;

            //Kakao OAuth 로그인 결과가 존재하지 않으면 회원가입 처리
            return userService.getUser(oAuthId)
                    .map(CustomUserDetails::from)
                    .orElseGet(() -> CustomUserDetails.from(userService.save(oAuthId, userRoleType)));
        };
    }
}
