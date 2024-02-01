package com.me.youtree.dto;

public record UserDto(
        Long id,
        String email,
        String password) {

    public static UserDto of(String email, String password) {
        return new UserDto(null, email, password);
    }
}
