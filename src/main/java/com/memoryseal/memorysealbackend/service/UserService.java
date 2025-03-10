package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
                .profileUrl((userCreateDTO.getProFileUrl()))
                .userActiveStatus(userCreateDTO.getUserActiveStatus())
                .build();
        return userJpaRepository.save(user);
    }


    public UserResponseDto getDetail(Long id) {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        return new UserResponseDto(user);
    }
}
