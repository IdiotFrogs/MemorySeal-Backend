package com.memoryseal.memorysealbackend.domain.user.controller;

import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserCreateDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserResponseDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserUpdateDto;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Tag(name = "User")
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
    public UserResponseDto getDetail() {
        return userService.getMyDetail();
    }

    @PutMapping("/{userId}")
    public UserUpdateDto updateUser(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }


}
