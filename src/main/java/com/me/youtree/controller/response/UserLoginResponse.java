package com.me.youtree.controller.response;

import com.me.youtree.dto.TokenDto;

public record UserLoginResponse(
        String accessToken,
        String refreshToken) {

    // dto -> response
    public static UserLoginResponse fromUser(TokenDto dto) {
        return new UserLoginResponse(
                dto.accessToken(),
                dto.refreshToken()
        );
    }
}
