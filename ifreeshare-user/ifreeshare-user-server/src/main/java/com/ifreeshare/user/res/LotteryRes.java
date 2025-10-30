package com.ifreeshare.user.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "抽奖信息的响应")
public class LotteryRes {

    @Schema(description = "抽奖活动的ID")
    private String id;

    @Schema(description = "抽奖活动的名称")
    private String lotteryName;

    @Schema(description = "抽奖活动的描述")
    private String lotteryDesc;

    @Schema(description = "抽奖活动的图片")
    private String lotteryPic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getLotteryDesc() {
        return lotteryDesc;
    }

    public void setLotteryDesc(String lotteryDesc) {
        this.lotteryDesc = lotteryDesc;
    }

    public String getLotteryPic() {
        return lotteryPic;
    }

    public void setLotteryPic(String lotteryPic) {
        this.lotteryPic = lotteryPic;
    }
}
