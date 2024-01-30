package com.me.youtree.controller;

import com.me.youtree.controller.request.TokenRequest;
import com.me.youtree.controller.request.UserLoginRequest;
import com.me.youtree.controller.request.UserRegisterRequest;
import com.me.youtree.controller.response.UserLoginResponse;
import com.me.youtree.dto.TokenDto;
import com.me.youtree.exception.Response;
import com.me.youtree.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Response<Void> register(@RequestBody UserRegisterRequest request) {
        userService.register(UserRegisterRequest.toDto(request));
        return Response.success();
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        TokenDto dto = userService.login(UserLoginRequest.toDto(request));
        return Response.success(UserLoginResponse.fromUser(dto));
    }

    @PostMapping("/reissue")
    public Response<UserLoginResponse> reIssue(@RequestBody TokenRequest request) {
        TokenDto dto = userService.reIssue(TokenRequest.toDto(request));
        return Response.success(UserLoginResponse.fromUser(dto));
    }
}
