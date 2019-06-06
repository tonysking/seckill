package com.imooc.seckill.model;

import lombok.Data;

/**
 * 暴露秒杀地址
 */
@Data
public class Exposer {

    // 是否开启秒杀
    private boolean exposed;
    // 加密
    private String md5;

    private long seckillId;

    // 系统当前时间（毫秒）
    private long nowTime;

    private long startTime;

    private long endTime;

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId, long nowTime, long startTime, long endTime) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.nowTime = nowTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }
}
