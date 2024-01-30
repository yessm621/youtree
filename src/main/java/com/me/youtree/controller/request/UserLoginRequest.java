package com.me.youtree.controller.request;

import com.me.youtree.dto.UserDto;

public record UserLoginRequest(
        String email,
        String password) {

    // request -> dto
    public static UserDto toDto(UserLoginRequest request) {
        return UserDto.of(
                request.email,
                request.password
        );
    }
}
