package com.ifreeshare.user.controller.customer;


import com.ifreeshare.user.entity.IsUsers;
import com.ifreeshare.user.res.UserInfoRes;
import com.ifreeshare.user.service.IsUsersService;
import com.ifreeshare.web.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户信息控制层
 *
 * @author
 * @since 2023-10-15 11:47:05
 */
@Tag(name="用户私有请求",description = "用户私有请求 -- 登陆后才能进行请求")
@RestController
@RequestMapping("/customer/user/")
public class PrivateCustomerController {

    @Autowired
    private IsUsersService isUsersService;

    @Autowired
    private RedissonClient redissonClient;


    @Operation(summary = "用户信息获取")
    @GetMapping("{id}")
    public ResultData<UserInfoRes> getUserInfo(){
        Long userId = Long.parseLong("1");
        IsUsers isUsers = this.isUsersService.queryById(userId);
        ResultData<UserInfoRes> userInfoResResultData = ResultData.defalut();
        userInfoResResultData.setData(parse(isUsers));
        return userInfoResResultData;
    }


    public UserInfoRes parse(IsUsers isUsers){
        UserInfoRes userInfoRes = new UserInfoRes();
        userInfoRes.setNickName(isUsers.getNickname());
        userInfoRes.setAvatar(isUsers.getAvatar());
        return userInfoRes;
    }


}
