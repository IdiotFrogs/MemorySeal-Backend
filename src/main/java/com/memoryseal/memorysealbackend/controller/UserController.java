package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody UserCreateDto userCreateDTO) {
        User user = userService.createUser(userCreateDTO);
    }

    @GetMapping("/find/{id}")
    public UserResponseDto getDetail(@PathVariable Long id) {
        return userService.getDetail(id);
    }


}
