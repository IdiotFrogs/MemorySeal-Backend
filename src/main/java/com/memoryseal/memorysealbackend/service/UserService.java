package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.dto.MyUserResponseDto;
import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
import com.memoryseal.memorysealbackend.dto.UserUpdateDto;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

//import java.security.Security;

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
                .build();
        return userJpaRepository.save(user);
    }


    public UserResponseDto getDetail(Long id) {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        return UserResponseDto.toDto(user);
    }
/*
    public MyUserResponseDto getMyDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        String username;
        if(principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        username = principal.toString();
        User user = userJpaRepository.findByNickname(username).orElse(null);

        return MyUserResponseDto.toDto(user);
    }
*/
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
