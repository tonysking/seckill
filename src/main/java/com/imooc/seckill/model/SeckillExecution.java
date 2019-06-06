package com.imooc.seckill.model;

import com.imooc.seckill.entity.SuccessKilled;
import com.imooc.seckill.enums.SeckillStateEnum;
import lombok.Data;

/**
 * 秒杀执行后结果
 */
@Data
public class SeckillExecution {

    //秒杀对象
    private long seckillId;

    //秒杀执行结果状态
    private int state;

    //状态标识
    private String stateInfo;

    //秒杀成功对象
    private SuccessKilled successKilled;

    /**
     * 秒杀成功
     * @param seckillId
     * @param seckillStateEnum
     * @param successKilled
     */
    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    /**
     * 秒杀失败
     * @param seckillId
     * @param seckillStateEnum
     */
    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
