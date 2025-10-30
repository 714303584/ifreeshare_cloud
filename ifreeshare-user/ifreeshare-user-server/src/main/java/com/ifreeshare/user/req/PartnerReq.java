package com.ifreeshare.user.req;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * 抽奖参与者
 */
@Schema(description = "抽奖参与者")
public class PartnerReq {

    //参与者名称
    @NotBlank(message = "抽奖编号不能为空")
    @Schema(description = "抽奖编号")
    private String lotteryNo;

    //参与者名称
    @NotBlank(message = "参与者名称不能为空")
    @Schema(description = "参与者名称")
    private String name;

    public String getLotteryNo() {
        return lotteryNo;
    }

    public void setLotteryNo(String lotteryNo) {
        this.lotteryNo = lotteryNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
