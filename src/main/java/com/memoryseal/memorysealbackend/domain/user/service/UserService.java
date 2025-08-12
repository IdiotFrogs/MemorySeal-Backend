package com.memoryseal.memorysealbackend.domain.user.service;

import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserCreateDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserResponseDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserUpdateDto;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserJpaRepository userJpaRepository;

    public User createUser(UserCreateDto userCreateDTO) {
        User user = User.builder()
                .nickname(userCreateDTO.getNickname())
                .email(userCreateDTO.getEmail())
                .profileUrl((userCreateDTO.getProfileUrl()))
                .userActiveStatus(userCreateDTO.getUserActiveStatus())
                .role(userCreateDTO.getRole())
                .build();
        return userJpaRepository.save(user);
    }


    public UserResponseDto getDetail(Long id) {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        return UserResponseDto.toDto(user);
    }

    public UserResponseDto getMyDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long currentUserId;
        if(principal instanceof UserDetails) {
            currentUserId = Long.valueOf(((UserDetails) principal).getUsername());
        }else if(principal instanceof Long){
            currentUserId = (Long) principal;
        }else if(principal instanceof String){
            currentUserId = Long.valueOf((String) principal);
        }else {
            throw new IllegalArgumentException("사용자 ID를 가져올 수 없음");
        }
        User user = userJpaRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        return UserResponseDto.toDto(user);
    }

    public UserUpdateDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        if(userUpdateDto.getNickname() != null) {
            user.setNickname(userUpdateDto.getNickname());
        }
        if(userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if(userUpdateDto.getProfileUrl() != null) {
            user.setProfileUrl(userUpdateDto.getProfileUrl());
        }

        userJpaRepository.save(user);

        return UserUpdateDto.toDto(user);
    }
}
