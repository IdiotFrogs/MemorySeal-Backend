package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.MyUserResponseDto;
import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
import com.memoryseal.memorysealbackend.dto.UserUpdateDto;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody UserCreateDto userCreateDTO) {
        User user = userService.createUser(userCreateDTO);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getDetail(@PathVariable Long userId) {
        return userService.getDetail(userId);
    }

    @GetMapping("/me")
    public MyUserResponseDto getDetail() {
        return userService.getMyDetail();
    }

    @PutMapping("/{userId}")
    public UserUpdateDto updateUser(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }


}
