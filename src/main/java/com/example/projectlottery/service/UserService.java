package com.example.projectlottery.service;

import com.example.projectlottery.domain.User;
import com.example.projectlottery.domain.type.UserRoleType;
import com.example.projectlottery.dto.response.UserResponse;
import com.example.projectlottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * OAuth 인증 정보를 바탕으로 회원 가입 처리
     * @param userId OAuth id
     * @param userRoleTypes user role type list
     */
    public UserResponse save(String userId, List<UserRoleType> userRoleTypes) {
        return UserResponse.from(userRepository.save(User.of(userId, userRoleTypes)));
    }

    /**
     * OAuth 인증 사용자 정보 조회
     * @param userId 사용자 id
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUser(String userId) {
        return userRepository.findById(userId).map(UserResponse::from);
    }

    /**
     * 관리자 페이지용 전체 회원 목록 조회
     * @return 전체 회원 목록
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .sorted(Comparator.comparing(UserResponse::createdAt))
                .toList();
    }
}
