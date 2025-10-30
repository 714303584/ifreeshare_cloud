package com.ifreeshare.user.res;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登陆的响应")
public class LoginRes {

    @Schema(description = "登陆成功后的token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
