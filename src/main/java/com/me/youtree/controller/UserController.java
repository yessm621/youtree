package com.me.youtree.controller;

import com.me.youtree.controller.request.UserRegisterRequest;
import com.me.youtree.exception.Response;
import com.me.youtree.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/reissue")
    public Response reIssue(HttpServletRequest request, HttpServletResponse response) {
        userService.reIssue(request, response);
        return Response.success();
    }
}
