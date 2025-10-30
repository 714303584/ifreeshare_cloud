package com.ifreeshare.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * 登陆的req
 */
@Schema(description = "用户登陆地址")
public class LoginReq {

    @NotEmpty(message = "登陆名不能为空")
    @Schema(description = "登陆名--")
    private String loginName;
    @NotBlank(message = "登陆密码不能为空")
    @Schema(description = "密码")
    private String password;


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
