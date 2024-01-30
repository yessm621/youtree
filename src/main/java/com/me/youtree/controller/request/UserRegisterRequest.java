package com.me.youtree.controller.request;

import com.me.youtree.dto.UserDto;

public record UserRegisterRequest(
        String email,
        String password) {

    // request -> dto
    public static UserDto toDto(UserRegisterRequest request) {
        return UserDto.of(
                request.email,
                request.password
        );
    }
}
