package com.ifreeshare.user.controller.customer;

import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.user.entity.IsUsers;
import com.ifreeshare.user.req.LoginReq;
import com.ifreeshare.user.req.RegisterReq;
import com.ifreeshare.user.res.LoginRes;
import com.ifreeshare.user.res.UserInfoRes;
import com.ifreeshare.user.service.IsUsersService;
import com.ifreeshare.web.HeaderBean;
import com.ifreeshare.web.ResponseCode;
import com.ifreeshare.web.ResultData;
import com.ifreeshare.web.ThreadLocalHeaderBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

/**
 * 客户信息控制层
 *
 * @author
 * @since 2023-10-15 11:47:05
 */
@Tag(name = "用户客户端API", description = "用于用户客户端的用户信息部分")
@RestController
@RequestMapping("/public/user/")
public class PublicCustomerController {
  @Autowired private IsUsersService isUsersService;

  @Autowired private RedissonClient redissonClient;

  /**
   * 用户登陆
   *
   * @param loginReq
   * @return
   */
  @PostMapping("login")
  public ResultData<LoginRes> login( @RequestBody  LoginReq loginReq) {
    ResultData<LoginRes> loginResResultData = new ResultData<LoginRes>();
    HeaderBean headerBean = ThreadLocalHeaderBean.get();
    IsUsers isUsers = isUsersService.queryByLoginName(loginReq.getLoginName());

    if (isUsers == null) {
      return ResultData.fail(
          ResponseCode.USER_NOT_FOUND.getCode(), ResponseCode.USER_NOT_FOUND.getMsg());
    } else {
      //这里需要加密密码
      if (!isUsers.getPassword().equals(loginReq.getPassword())) {
        return ResultData.fail(
            ResponseCode.USER_NOT_FOUND.getCode(), ResponseCode.USER_NOT_FOUND.getMsg());
      } else {
        LoginRes loginRes = new LoginRes();
        loginRes.setToken(JwtUtils.getToken(isUsers.getId().toString()));
        loginResResultData.setData(loginRes);
        return loginResResultData;
      }
    }
  }

  @PostMapping("register")
  public ResultData<String> register(RegisterReq registerReq) {
    ResultData<String> resultData = ResultData.defalut();
    IsUsers isUsers = new IsUsers();
    isUsers.setNickname(registerReq.getLoginName());
    isUsers.setLoginName(registerReq.getLoginName());
    isUsers.setPassword(registerReq.getPassword());
    isUsers.setStatus(1);
    isUsers.setAvatar(registerReq.getAvatar());
    isUsersService.insert(isUsers);
    return resultData;
  }

  @PostMapping("sendCode")
  public ResultData<String> sendCode(LoginReq loginReq) {

    return ResultData.defalut();

  }

}
