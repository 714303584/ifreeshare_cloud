package com.ifreeshare.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/** 抽奖请求 */
@Schema(description = "抽奖信息请求")
public class LotteryReq {

  // 参与者名称
  @NotBlank(message = "抽奖编号不能为空")
  @Schema(description = "抽奖编号")
  private String lotteryNo;

  // 名称
  @NotBlank(message = "活动名称不能为空")
  @Schema(description = "抽奖名称")
  private String lotteryName;

  // 图片
  //    @NotBlank(message = "抽奖活动图片")
  @Schema(description = "抽奖图片")
  private String lotteryPic;

  // 图片
  //    @NotBlank(message = "抽奖编号不能为空")
  @Schema(description = "抽奖描述")
  private String lotteryDesc;

  public String getLotteryNo() {
    return lotteryNo;
  }

  public void setLotteryNo(String lotteryNo) {
    this.lotteryNo = lotteryNo;
  }

  public String getLotteryName() {
    return lotteryName;
  }

  public void setLotteryName(String lotteryName) {
    this.lotteryName = lotteryName;
  }

  public String getLotteryPic() {
    return lotteryPic;
  }

  public void setLotteryPic(String lotteryPic) {
    this.lotteryPic = lotteryPic;
  }

  public String getLotteryDesc() {
    return lotteryDesc;
  }

  public void setLotteryDesc(String lotteryDesc) {
    this.lotteryDesc = lotteryDesc;
  }
}
