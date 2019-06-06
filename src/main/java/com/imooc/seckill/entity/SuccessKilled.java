package com.imooc.seckill.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SuccessKilled {
    private long seckillId;
    private long userPhone;
    private short state;
    private Date createTime;
    //Mybatis里面的级联要在xml文件内配置
    // 多对一
    private Seckill seckill;
}
