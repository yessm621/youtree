package com.me.youtree.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpDto {

    private String email;
    private String password;
    private String nickname;
}
