package com.me.youtree.controller.request;

import com.me.youtree.dto.TokenDto;

public record TokenRequest(
        String accessToken,
        String refreshToken) {

    // request -> dto
    public static TokenDto toDto(TokenRequest request) {
        return TokenDto.of(
                request.accessToken,
                request.refreshToken
        );
    }
}
