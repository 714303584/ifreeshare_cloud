package com.ifreeshare.user.req;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登陆的req
 */
@Schema(description = "用户登陆地址")
public class RegisterReq {

    @Schema(description = "登陆名--")
    private String loginName;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "头像")
    private String avatar;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
