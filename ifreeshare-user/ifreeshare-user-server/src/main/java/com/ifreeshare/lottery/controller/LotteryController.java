package com.ifreeshare.lottery.controller;


import com.ifreeshare.user.req.LotteryReq;
import com.ifreeshare.user.req.PartnerReq;
import com.ifreeshare.user.res.LotteryRes;
import com.ifreeshare.web.ResultData;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 抽奖控制层
 *
 * @author
 * @since 2023-10-15 11:47:05
 */
@Tag(name = "抽奖客户端API", description = "用于用户客户端的抽奖信息部分")
@RestController
@RequestMapping("/public/lottery/")
public class LotteryController {

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 添加抽奖用户
     * @param id
     * @return
     */
    @GetMapping("get")
    public ResultData info(String id) {

        //获取运行中的lottery
        RMap<String,LotteryRes> activeLottery = redissonClient.getMap("active_lottery");

       LotteryRes lotteryRes = activeLottery.get(id);
       if(lotteryRes == null){
           //获取失败
           return ResultData.fail(-1,"失败");
       }else{
           //获取成功
           ResultData resultData = new ResultData();
           resultData.setData(lotteryRes);
           return resultData;

       }

    }

    /**
     * 添加抽奖活动
     * @param lotteryReq
     * @return
     */
    @PostMapping("add")
    public ResultData add(@RequestBody LotteryReq lotteryReq) {
        //获取运行中的lottery
        RMap<String,LotteryRes> activeLottery = redissonClient.getMap("active_lottery");
        LotteryRes lotteryRes = activeLottery.put("1",reqParseToRes(lotteryReq));
        //添加成功
        if(lotteryRes != null){
            //返回成功
            return ResultData.defalut();
        }
        //添加失败
        return ResultData.fail(-1,"失败");
    }

    public LotteryRes reqParseToRes(LotteryReq lotteryReq){
        LotteryRes lotteryRes = new LotteryRes();
        lotteryRes.setId(lotteryReq.getLotteryNo());
        lotteryRes.setLotteryName(lotteryReq.getLotteryName());
        lotteryRes.setLotteryPic(lotteryReq.getLotteryPic());
        lotteryRes.setLotteryDesc(lotteryReq.getLotteryDesc());
        return lotteryRes;


    }


    /**
     * 添加抽奖用户
     * @param partnerReq
     * @return
     */
    @PostMapping("join")
    public ResultData join(@RequestBody PartnerReq partnerReq) {

        //获取key
        RSet<String> lotterySet = redissonClient.getSet(partnerReq.getLotteryNo());
        //添加抽奖人的名字
        if(lotterySet.add(partnerReq.getName())){
            //返回成功
            return ResultData.defalut();
        }
        //没有添加成功则失败
        return ResultData.fail(-1,"失败");
    }

    /**
     * 开奖
     * @return
     */
    @PostMapping("open")
    public ResultData open(@RequestBody LotteryReq lotteryReq) {
        //获取key
        RSet<String> lotterySet = redissonClient.getSet(lotteryReq.getLotteryNo());
        //获取一个中奖者
        if(lotterySet.size() > 0){
            //获取中奖者
            String part = lotterySet.random();
            //删除中奖者
            lotterySet.remove(part);
            //保存中奖者
            RSet<String> luckyGuys = redissonClient.getSet(lotteryReq.getLotteryNo()+"_luckyGuys");
            luckyGuys.add(part);
            //没有添加成功则失败
            return ResultData.defalut();
        }else{
            //没有参与者
            return ResultData.fail(-1,"无参与者-开奖失败");
        }

    }





}
